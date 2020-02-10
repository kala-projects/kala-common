module asia.kala.collection {
    requires static org.jetbrains.annotations;
    requires static kotlin.annotations.jvm;

    requires transitive asia.kala.base;

    exports asia.kala.collection;
    exports asia.kala.collection.mutable;
    exports asia.kala.collection.immutable;
}