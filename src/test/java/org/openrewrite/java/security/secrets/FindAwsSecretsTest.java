/*
 * Copyright 2022 the original author or authors.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * https://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.openrewrite.java.security.secrets;

import org.junit.jupiter.api.Test;
import org.openrewrite.DocumentExample;
import org.openrewrite.config.Environment;
import org.openrewrite.test.RecipeSpec;
import org.openrewrite.test.RewriteTest;

import static org.openrewrite.java.Assertions.java;
import static org.openrewrite.yaml.Assertions.yaml;

public class FindAwsSecretsTest implements RewriteTest {

    @Override
    public void defaults(RecipeSpec spec) {
        spec.recipe(Environment.builder()
          .scanRuntimeClasspath("org.openrewrite.java.security.secrets")
          .build()
          .activateRecipes("org.openrewrite.java.security.secrets.FindAwsSecrets"));
    }

    @DocumentExample
    @Test
    void awsSecrets() {
        rewriteRun(
          //language=yaml
          yaml(
            """
              env1:
                aws_access_key: wJalrXUtnFEMI/K7MDENG/bPxRfiCYEXAMPLEKEY
              env2:
                aws_access_key: wJalrXUtnFEMI/K7MDENG/bPxRfiCYEXAMPLEKEYa
              evn3:
                aws_access_key: wJalrXUtnFEMI/K7MDENG/bPxRfiCYEXAMPLEKE
              """,
            """
              env1:
                ~~(AWS access key)~~>aws_access_key: wJalrXUtnFEMI/K7MDENG/bPxRfiCYEXAMPLEKEY
              env2:
                aws_access_key: wJalrXUtnFEMI/K7MDENG/bPxRfiCYEXAMPLEKEYa
              evn3:
                aws_access_key: wJalrXUtnFEMI/K7MDENG/bPxRfiCYEXAMPLEKE
              """
          ),
          //language=java
          java(
            """
              class T {
                  String[] awsSecrets = {
                      "AKIAZZZZZZZZZZZZZZZZ",
                      "akiazzzzzzzzzzzzzzzz",
                      "AKIAZZZ",
                  };
              }
              """,
            """
              class T {
                  String[] awsSecrets = {
                      /*~~(AWS access key)~~>*/"AKIAZZZZZZZZZZZZZZZZ",
                      "akiazzzzzzzzzzzzzzzz",
                      "AKIAZZZ",
                  };
              }
              """
          )
        );
    }
}
