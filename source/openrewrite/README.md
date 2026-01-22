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

(parallel execution is currently buggy, see https://github.com/openrewrite/rewrite-gradle-plugin/issues/212 )