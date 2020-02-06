package asia.kala.gen

import com.google.common.io.CharSource
import com.google.googlejavaformat.java.Formatter
import com.google.googlejavaformat.java.JavaFormatterOptions
import com.squareup.javapoet.*
import org.jetbrains.annotations.NotNull

import javax.lang.model.element.Modifier
import java.io.File
import java.io.Serializable
import java.nio.file.Files
import java.nio.file.StandardOpenOption
import java.util.*
import java.util.function.IntFunction

class TuplesGenerator(private val baseDir: File, private val packageName: String, private val limit: Int) {
    companion object {
        @JvmStatic
        fun generate(baseDir: File, packageName: String, limit: Int) {
            TuplesGenerator(baseDir, packageName, limit).generate()
        }
    }

    fun generate() {
        genTupleClass()
        for (i in 1..limit) {
            TupleNGenerator(i).gen().write()
        }
    }

    private val tupleName = ClassName.get(packageName, "Tuple")
    private val tuple0Name = ClassName.get(packageName, "Tuple0")
    private val hlistName = ClassName.get(packageName, "HList")

    private inner class TupleNGenerator(val i: Int) {
        private val tcb = TypeSpec.classBuilder("Tuple$i")
        private val tps = (1..i).map { TypeVariableName.get("T$it") }.toTypedArray()
        private val tailType = if (i == 1) {
            ClassName.get(packageName, "Tuple0")
        } else {
            ParameterizedTypeName.get(
                    ClassName.get(packageName, "Tuple${i - 1}"),
                    *tps.drop(1).toTypedArray()
            )
        }

        private fun t(i: Int) = tps[i - 1]

        fun gen(): TupleNGenerator {
            genClassBase()
            genSerialVersionUID()
            genNarrowMethods()
            genFields()
            genConstructor()
            genTupleMethods()
            genHListMethods()
            if (i == 2) {
                genMapEntryMethods()
            }
            genObjectMethods()

            return this
        }

        private fun genClassBase() {
            tcb.addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                    .addTypeVariables(tps.toList())
                    .superclass(
                            ParameterizedTypeName.get(
                                    hlistName,
                                    t(1), tailType
                            )
                    )
                    .addSuperinterface(Serializable::class.java)
                    .addJavadoc(" A tuple of ${numeral(i)} ${plural(i, "element")}.\n")
                    .addJavadoc("\n")

            for (it in 1..i) {
                tcb.addJavadoc("@param <T$it> type of the ${ordinal(it)} element\n")
            }

            tcb.addJavadoc("\n")
            tcb.addJavadoc("@author Glavo\n")
        }

        private fun genSerialVersionUID() {
            tcb.addField(FieldSpec.builder(Long::class.java, "serialVersionUID")
                    .addModifiers(Modifier.PRIVATE, Modifier.STATIC, Modifier.FINAL)
                    .initializer(uid("$packageName.Tuple$i"))
                    .build()
            )
        }

        private fun genNarrowMethods() {
            fun r(n: Int = 1): ParameterizedTypeName = ParameterizedTypeName.get(
                    hlistName,
                    WildcardTypeName.subtypeOf(t(n)),
                    WildcardTypeName.subtypeOf(
                            if (n == i) tuple0Name else r(n + 1)
                    )
            )

            val ret = ParameterizedTypeName.get(ClassName.get(packageName, "Tuple$i"), *tps)
            tcb.addMethod(MethodSpec.methodBuilder("narrow")
                    .addTypeVariables(tps.toList())
                    .addParameter(r(), "tuple")
                    .returns(ret)
                    .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                    .addAnnotation(Annotations.unchecked())
                    .addStatement("return (\$T) tuple", ret)
                    .build()
            )
        }

        private fun genFields() {
            for (it in 1..i) {
                tcb.addField(FieldSpec.builder(t(it), "_$it")
                        .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                        .addJavadoc("The ${ordinal(it)} element of this tuple.\n")
                        .build()
                )
            }
        }

        private fun genConstructor() {
            tcb.addMethod(MethodSpec.constructorBuilder()
                    .addModifiers(Modifier.PUBLIC)
                    .addParameters((1..i).map { ParameterSpec.builder(t(it), "t$it").build() })
                    .apply { (1..i).forEach { addStatement("this._$it = t$it") } }
                    .addJavadoc("Constructs a tuple of ${numeral(i)} ${plural(i, "element")}.\n")
                    .addJavadoc("\n")
                    .apply { (1..i).forEach { addJavadoc("@param t$it the ${ordinal(it)} element\n") } }
                    .build()
            )
        }

        private fun genTupleMethods() {
            // arity
            tcb.addMethod(MethodSpec.methodBuilder("arity")
                    .returns(TypeName.INT)
                    .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                    .addAnnotation(Override::class.java)
                    .addStatement("return $i")
                    .addJavadoc("{@inheritDoc}\n")
                    .build()
            )

            // elementAt
            tcb.addMethod(MethodSpec.methodBuilder("elementAt")
                    .addTypeVariable(TypeVariableName.get("U"))
                    .addParameter(Int::class.java, "index")
                    .returns(TypeVariableName.get("U"))
                    .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                    .addAnnotation(Override::class.java)
                    .addAnnotation(Annotations.unchecked())
                    .apply {
                        beginControlFlow("switch (index)")
                        (0 until i).forEach {
                            addStatement("case $it:\n\$<return (U) _${it + 1}\$>")
                        }
                        addStatement("default:\n\$<throw new \$T(\"Index out of range: \" +index)\$>", IndexOutOfBoundsException::class.java)
                        endControlFlow()
                    }
                    .addJavadoc("{@inheritDoc}\n")
                    .build()
            )

            // toJavaArray
            tcb.addMethod(MethodSpec.methodBuilder("toJavaArray")
                    .addTypeVariable(TypeVariableName.get("U"))
                    .addParameter(
                            ParameterSpec.builder(
                                    ParameterizedTypeName.get(ClassName.get(IntFunction::class.java),
                                            ArrayTypeName.of(TypeVariableName.get("U"))),
                                    "generator"
                            ).addAnnotation(NotNull::class.java).build()
                    )
                    .returns(ArrayTypeName.of(TypeVariableName.get("U")))
                    .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                    .addAnnotation(NotNull::class.java)
                    .addAnnotation(Override::class.java)
                    .addAnnotation(Annotations.unchecked())
                    .apply {
                        addStatement("U[] arr = generator.apply(arity())")
                        (0 until i).forEach {
                            addStatement("arr[$it] = (U) this._${it + 1}")
                        }
                        addStatement("return arr")
                    }
                    .addJavadoc("{@inheritDoc}\n")
                    .build()
            )

            // components
            for (it in 1..i) {
                tcb.addMethod(MethodSpec.methodBuilder("component$it")
                        .returns(t(it))
                        .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                        .addJavadoc("Returns the ${ordinal(it)} element of this tuple.\n")
                        .addJavadoc("\n")
                        .addJavadoc("@return the ${ordinal(it)} element of this tuple\n")
                        .addStatement("return _$it")
                        .build()
                )
            }
        }

        private fun genHListMethods() {
            tcb.addMethod(MethodSpec.methodBuilder("head")
                    .returns(TypeVariableName.get("T1"))
                    .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                    .addAnnotation(Override::class.java)
                    .addStatement("return _1")
                    .addJavadoc("{@inheritDoc}\n")
                    .build()
            )

            tcb.addMethod(MethodSpec.methodBuilder("tail")
                    .returns(tailType)
                    .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                    .addAnnotation(NotNull::class.java)
                    .addAnnotation(Override::class.java)
                    .addStatement("return Tuple.of(" + (2..i).joinToString(", ") { "_$it" } + ")")
                    .addJavadoc("{@inheritDoc}\n")
                    .build()
            )

            tcb.addMethod(MethodSpec.methodBuilder("cons")
                    .addTypeVariable(TypeVariableName.get("H"))
                    .addParameter(TypeVariableName.get("H"), "head")
                    .apply {
                        if (i == limit) {
                            returns(ParameterizedTypeName.get(
                                    hlistName,
                                    TypeVariableName.get("H"),
                                    ParameterizedTypeName.get(ClassName.get(packageName, "Tuple$i"), *tps)
                            )
                            )
                        } else {
                            returns(ParameterizedTypeName.get(ClassName.get(packageName, "Tuple${i + 1}"),
                                    *(arrayOf(TypeVariableName.get("H")) + tps)
                            )
                            )
                        }
                    }
                    .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                    .addAnnotation(NotNull::class.java)
                    .addAnnotation(Annotations.contract("_ -> new"))
                    .addAnnotation(Override::class.java)
                    .apply {
                        if (i == limit) {
                            addStatement("Object[] arr = new Object[${i + 1}]")
                            addStatement("arr[0] = head")
                            (1..i).forEach {
                                addStatement("arr[$it] = _$it")
                            }
                            addStatement("return new TupleXXL(arr).cast()")
                        } else {
                            addStatement("return new Tuple${i + 1}<>(" +
                                    (arrayOf("head") + (1..i).map { "_$it" }).joinToString(", ")
                                    + ")")
                        }
                    }
                    .addJavadoc("{@inheritDoc}\n")
                    .build()
            )
        }

        private fun genMapEntryMethods() {
            // Tuple2 implement Map.Entry
            tcb.addSuperinterface(
                    ParameterizedTypeName.get(ClassName.get(Map.Entry::class.java),
                            TypeVariableName.get("T1"), TypeVariableName.get("T2")
                    )
            )

            tcb.addMethod(MethodSpec.methodBuilder("getKey")
                    .returns(TypeVariableName.get("T1"))
                    .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                    .addAnnotation(Override::class.java)
                    .addStatement("return _1")
                    .addJavadoc("{@inheritDoc}\n")
                    .build()
            )

            tcb.addMethod(MethodSpec.methodBuilder("getValue")
                    .returns(TypeVariableName.get("T2"))
                    .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                    .addAnnotation(Override::class.java)
                    .addStatement("return _2")
                    .addJavadoc("{@inheritDoc}\n")
                    .build()
            )

            tcb.addMethod(MethodSpec.methodBuilder("setValue")
                    .returns(TypeVariableName.get("T2"))
                    .addParameter(TypeVariableName.get("T2"), "value")
                    .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                    .addAnnotation(Override::class.java)
                    .addAnnotation(java.lang.Deprecated::class.java)
                    .addAnnotation(Annotations.contract("_ -> fail"))
                    .addJavadoc("Used to override {@link java.util.Map # setValue}.\n")
                    .addJavadoc("Tuples are immutable, calling this method will always fail.\n")
                    .addJavadoc("\n")
                    .addJavadoc("@throws UnsupportedOperationException when calling this method\n")
                    .addException(UnsupportedOperationException::class.java)
                    .addStatement("throw new \$T(\"Tuple2.setValue\")", UnsupportedOperationException::class.java)
                    .build()
            )
        }

        private fun genObjectMethods() {
            // hashCode
            tcb.addMethod(MethodSpec.methodBuilder("hashCode")
                    .returns(Int::class.java)
                    .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                    .addAnnotation(Override::class.java)
                    .apply {
                        addStatement("int hash = 0")
                        (1..i).forEach {
                            addStatement("hash = 31 * hash + Objects.hashCode(_$it)")
                        }
                        addStatement("return hash")
                    }
                    .addJavadoc("{@inheritDoc}\n")
                    .build()
            )

            // equals
            tcb.addMethod(MethodSpec.methodBuilder("equals")
                    .returns(Boolean::class.java)
                    .addParameter(ParameterSpec.builder(Object::class.java, "o").build())
                    .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                    .addAnnotation(Override::class.java)
                    .apply {
                        val ut = "Tuple$i<" + CharArray(i) { '?' }.joinToString(", ") + ">"
                        beginControlFlow("if (this == o)")
                        addStatement("return true")
                        endControlFlow()
                        beginControlFlow("if (!(o instanceof $ut))")
                        addStatement("return false")
                        endControlFlow()
                        addStatement("$ut t = ($ut) o")

                        addStatement(
                                (1..i).joinToString(" && ", "return ") { "\$T.equals(_$it, t._$it)" },
                                *Array(i) { Objects::class.java }
                        )
                    }
                    .addJavadoc("{@inheritDoc}\n")
                    .build()
            )

            // toString
            tcb.addMethod(MethodSpec.methodBuilder("toString")
                    .returns(String::class.java)
                    .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                    .addAnnotation(Override::class.java)
                    .addStatement(
                            """return "(" + """ + (1..i).joinToString(separator = """ + ", " + """) { "_$it" } + """ + ")""""
                    )
                    .addJavadoc("{@inheritDoc}\n")
                    .build()
            )
        }

        fun write() {
            JavaFile.builder(packageName, tcb.build())
                    .indent("    ")
                    .skipJavaLangImports(true)
                    .build()
                    .writeTo(baseDir)
        }
    }

    private fun genTupleClass() {
        val tcb = TypeSpec.classBuilder("Tuple")
                .addSuperinterface(Serializable::class.java)
                .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)

                .addJavadoc("The base class of all tuples.\n")
                .addJavadoc("\n")
                .addJavadoc("@author Glavo\n")
                .addJavadoc("@see HList\n")

        tcb.addMethod(MethodSpec.constructorBuilder().build())

        // arity
        tcb.addMethod(MethodSpec.methodBuilder("arity")
                .returns(Int::class.java)
                .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
                .addJavadoc("Returns the number of elements of this {@code Tuple}.\n")
                .addJavadoc("\n")
                .addJavadoc("@return the number of elements of this {@code Tuple}\n")
                .build()
        )

        // elementAt
        tcb.addMethod(MethodSpec.methodBuilder("elementAt")
                .addTypeVariable(TypeVariableName.get("U"))
                .addParameter(Int::class.java, "index")
                .returns(TypeVariableName.get("U"))
                .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
                .addJavadoc("Returns the element at the specified position in this {@code Tuple}.\n")
                .addJavadoc("\n")
                .addJavadoc("@return the element at the specified position in this {@code Tuple}\n")
                .addJavadoc("@throws IndexOutOfBoundsException if the index is out of range ({@code index < 0 || index >= arity()})\n")
                .addJavadoc("@apiNote this method is not type safe, please use {@code Tuple.componentN(tuple)} \n")
                .addJavadoc("to get the Nth element of the tuple (when {@code N <= 18}).\n")
                .build()
        )

        // cons
        tcb.addMethod(MethodSpec.methodBuilder("cons")
                .addTypeVariable(TypeVariableName.get("H"))
                .addParameter(TypeVariableName.get("H"), "head")
                .returns(ParameterizedTypeName.get(ClassName.get(packageName, "HList"),
                        TypeVariableName.get("H"), WildcardTypeName.subtypeOf(ClassName.get(packageName, "Tuple"))
                ))
                .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
                .addJavadoc("Return a new tuple by prepending the head to `this` tuple.\n")
                .addJavadoc("\n")
                .addJavadoc("@return a new tuple by prepending the head to `this` tuple")
                .build()
        )

        // toJavaArray

        tcb.addMethod(MethodSpec.methodBuilder("toJavaArray")
                .returns(Array<Any>::class.java)
                .addModifiers(Modifier.PUBLIC)
                .addStatement("return toJavaArray(Object[]::new)")
                .addJavadoc("Returns an array containing all of the elements in this tuple.\n")
                .addJavadoc("@return an array containing all of the elements in this tuple\n")
                .addAnnotation(NotNull::class.java)
                .addAnnotation(Annotations.contract("_ -> new"))
                .build()
        )

        tcb.addMethod(MethodSpec.methodBuilder("toJavaArray")
                .addTypeVariable(TypeVariableName.get("U"))
                .addParameter(ParameterSpec.builder(
                        ParameterizedTypeName.get(ClassName.get(IntFunction::class.java),
                                ArrayTypeName.of(TypeVariableName.get("U"))),
                        "generator")
                        .addAnnotation(NotNull::class.java)
                        .build()
                )
                .returns(ArrayTypeName.of(TypeVariableName.get("U")))
                .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
                .addJavadoc("Returns an array containing all of the elements in this tuple,\n")
                .addJavadoc("using the provided {@code generator} function to allocate the returned array.\n")
                .addJavadoc("@return an array containing all of the elements in this tuple\n")
                .addJavadoc("@throws ArrayStoreException if any element of this tuple cannot be stored \n")
                .addJavadoc("in the generated array because the runtime type does not match\n")
                .addAnnotation(NotNull::class.java)
                .build()
        )


        // of

        tcb.addMethod(MethodSpec.methodBuilder("of")
                .returns(ClassName.get(packageName, "Tuple0"))
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .addStatement("return Tuple0.INSTANCE")
                .build()
        )
        for (it in 1..limit) {
            val tps = (1..it).map { n -> TypeVariableName.get("T$n") }.toTypedArray()
            val args = (1..it).map { n -> "t$n" }.toTypedArray()
            tcb.addMethod(MethodSpec.methodBuilder("of")
                    .addTypeVariables(tps.toList())
                    .addParameters(tps.zip(args).map { (t, n) -> ParameterSpec.builder(t, n).build() })
                    .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                    .returns(ParameterizedTypeName.get(ClassName.get(packageName, "Tuple$it"), *tps))
                    .addStatement("return new Tuple$it<>(" + args.joinToString(", ") + ")")
                    .addJavadoc("Creates a tuple of ${numeral(it)} ${plural(it, "element")}.\n")
                    .addJavadoc("\n")
                    .apply { (1..it).forEach { n -> addJavadoc("@param <T$n> type of the ${ordinal(n)} element\n") } }
                    .apply { (1..it).forEach { n -> addJavadoc("@param t$n the ${ordinal(n)} element\n") } }
                    .addJavadoc("@return a tuple of ${numeral(it)} ${plural(it, "element")}\n")
                    .addAnnotation(NotNull::class.java)
                    .addAnnotation(Annotations.contract(
                            Array(it) { "_" }.joinToString(", ", postfix = " -> new")
                    ))
                    .build()
            )
        }

        tcb.addMethod(MethodSpec.methodBuilder("of")
                .addTypeVariable(TypeVariableName.get("T", tupleName))
                .addParameter(Array<Any>::class.java, "values")
                .varargs()
                .returns(TypeVariableName.get("T"))
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .addAnnotation(Annotations.unchecked())
                .addStatement("\$T.requireNonNull(values, \"values\")", Objects::class.java)
                .beginControlFlow("switch (values.length)")
                .apply {
                    addStatement("case 0:\n\$<return (T) Tuple0.INSTANCE\$>")
                    for (it in 1..limit) {
                        addStatement("case $it:\n\$<return (T) new Tuple$it<>(" +
                                (0 until it).joinToString(", ") { n -> "values[$n]" } +
                                ")\$>"
                        )
                    }
                    addStatement("default:\n\$<return (T) new TupleXXL(values.clone())\$>")
                }
                .endControlFlow()
                .addAnnotation(NotNull::class.java)
                .addAnnotation(Annotations.contract("_ -> new"))
                .build()
        )

        // componentN
        for (it in 1..18) {
            val t = TypeVariableName.get("T")
            fun tn(n: Int = 1): ParameterizedTypeName =
                    if (n == it) {
                        ParameterizedTypeName.get(hlistName, t, WildcardTypeName.subtypeOf(Object::class.java))
                    } else {
                        ParameterizedTypeName.get(hlistName, WildcardTypeName.subtypeOf(Object::class.java), WildcardTypeName.subtypeOf(tn(n + 1)))
                    }

            tcb.addMethod(MethodSpec.methodBuilder("component$it")
                    .addTypeVariable(t)
                    .addParameter(ParameterSpec.builder(tn(), "tuple").addAnnotation(NotNull::class.java).build())
                    .returns(t)
                    .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                    .addStatement("\$T.requireNonNull(tuple)", Objects::class.java)
                    .addStatement("return tuple.elementAt(${it - 1})")
                    .addJavadoc("Return the ${ordinal(it)} element of the {@code tuple}.\n")
                    .addJavadoc("\n")
                    .addJavadoc("@param <T> type of the element\n")
                    .addJavadoc("@param tuple a {@code Tuple}\n")
                    .addJavadoc("@return the ${ordinal(it)} element of the {@code tuple}\n")
                    .build()
            )
        }

        Files.newBufferedWriter(
                Files.createDirectories(
                        baseDir.toPath().resolve(packageName.replace('.', '/'))).resolve("Tuple.java"),
                StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING).use {

            val str = JavaFile.builder(packageName, tcb.build())
                    .indent("    ")
                    .skipJavaLangImports(true)
                    .build()
                    .toString()
            Formatter(JavaFormatterOptions.builder().style(JavaFormatterOptions.Style.AOSP).build())
                    .formatSource(CharSource.wrap(str), WriterSink(it))
        }
    }
}
