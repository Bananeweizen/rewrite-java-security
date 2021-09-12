/*
 * Copyright 2021 the original author or authors.
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
package org.openrewrite.java.security.search

import org.junit.jupiter.api.Test
import org.openrewrite.Recipe
import org.openrewrite.java.JavaParser
import org.openrewrite.java.JavaRecipeTest

class FindVulnerableJacksonJsonTypeInfoTest : JavaRecipeTest {
    override val parser: JavaParser
        get() = JavaParser.fromJavaVersion()
            .classpath("jackson-annotations")
            .build()

    override val recipe: Recipe
        get() = FindVulnerableJacksonJsonTypeInfo()

    @Test
    fun idClass() = assertChanged(
        before = """
            import java.util.List;
            import com.fasterxml.jackson.annotation.JsonTypeInfo;
            import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;
            
            class PenetrationTesting {
                @JsonTypeInfo(use = Id.CLASS)
                Object name;
                
                /*~~>*/@JsonTypeInfo(use = Id.CLASS)
                List<Object> names;
            
                int age;
            }
        """,
        after = """
            import java.util.List;
            import com.fasterxml.jackson.annotation.JsonTypeInfo;
            import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;
            
            class PenetrationTesting {
                /*~~>*/@JsonTypeInfo(use = Id.CLASS)
                Object name;
                
                /*~~>*/@JsonTypeInfo(use = Id.CLASS)
                List<Object> names;
            
                int age;
            }
        """
    )
}
