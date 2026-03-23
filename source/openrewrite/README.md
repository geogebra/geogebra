# OpenRewrite recipes

Recipes in this project can be used to refactor shared, web and desktop projects.

Example 1, internal recipe:

```shell
./gradlew -p source/shared rewriteRun "-Drewrite.activeRecipe=org.geogebra.openrewrite.recipes.NormalizeAnnotations" --no-parallel
```

Example 2, external recipe from https://docs.openrewrite.org/recipes/staticanalysis :

```shell
./gradlew -p source/shared rewriteRun "-Drewrite.activeRecipe=org.openrewrite.staticanalysis.FinalizePrivateFields" --no-parallel
```

Example 3, migrate JUnit 4 tests to JUnit 5 in the shared module:

```shell
./gradlew -p source/shared rewriteRun "-Drewrite.activeRecipe=org.geogebra.openrewrite.MigrateToJUnit5" --no-parallel
```

To run the migration across all modules:

```shell
./gradlew rewriteRun "-Drewrite.activeRecipe=org.geogebra.openrewrite.MigrateToJUnit5" --no-parallel
```

(parallel execution is currently buggy, see https://github.com/openrewrite/rewrite-gradle-plugin/issues/212 )