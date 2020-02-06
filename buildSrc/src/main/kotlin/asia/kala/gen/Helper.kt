package asia.kala.gen

import com.google.common.io.CharSink
import com.squareup.javapoet.AnnotationSpec
import org.jetbrains.annotations.Contract
import java.io.Writer
import java.nio.charset.StandardCharsets
import java.security.MessageDigest
import javax.lang.model.element.AnnotationMirror

fun ordinal(i: Int): String {
    assert(i > 0)
    return when (i) {
        1 -> "1st"
        2 -> "2nd"
        3 -> "3rd"
        else -> "${i}th"
    }
}

fun numeral(i: Int): String {
    assert(i >= 0)
    return if (i == 0) "no" else i.toString()
}

fun plural(i: Int, str: String): String {
    assert(i >= 0)
    return if (i == 1) str else str + "s"
}

fun uid(name: String): String {
    val md = MessageDigest.getInstance("SHA-256")

    var l = 0L
    for (b in md.digest(name.toByteArray(StandardCharsets.UTF_8))) {
        l = l * 31 + b
    }

    return l.toString() + "L"
}

object Annotations {
    fun contract(
            value: String = "",
            pure: Boolean = false,
            mutates: String = ""
    ): AnnotationSpec =
            AnnotationSpec.builder(Contract::class.java)
                    .apply { if (value != "") addMember("value", '"' + value + '"') }
                    .apply { if (pure) addMember("pure", pure.toString()) }
                    .apply { if (mutates != "") addMember("mutates", '"' + mutates + '"') }
                    .build()

    fun unchecked(): AnnotationSpec =
            AnnotationSpec.builder(SuppressWarnings::class.java)
                    .addMember("value", "\"unchecked\"")
                    .build()
}

class WriterSink(private val writer: Writer) : CharSink() {
    override fun openStream(): Writer = writer
}
