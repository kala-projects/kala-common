module kala.collection {
    requires kotlin.stdlib;

    requires transitive asia.kala.collection;

    exports kala.collection;
    exports kala.collection.mutable;
    exports kala.collection.immutable;

}