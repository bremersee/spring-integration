/*
 * Copyright 2019 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.bremersee.ldaptive.app;

import java.util.Set;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * The test group.
 *
 * @author Christian Bremer
 */
@Data
@NoArgsConstructor
public class Group {

  private String cn;

  private String ou;

  private Set<String> members;

}
