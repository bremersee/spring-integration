/*
 * Copyright 2024 the original author or authors.
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

package org.bremersee.ldaptive.serializable;

import java.io.Serial;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;
import lombok.Getter;
import lombok.ToString;
import org.ldaptive.LdapAttribute;
import org.ldaptive.LdapUtils;

/**
 * A serializable ldap attribute.
 *
 * @author Christian Bremer
 */
@ToString(onlyExplicitlyIncluded = true)
public class SerLdapAttr implements Serializable {

  @Serial
  private static final long serialVersionUID = 1L;

  /**
   * Attribute name.
   */
  @ToString.Include
  @Getter
  private final String attributeName;

  /**
   * Attribute values.
   */
  private final Collection<byte[]> attributeValues;

  /**
   * Whether this attribute is binary and string representations should be base64 encoded.
   */
  @ToString.Include
  @Getter
  private final boolean binary;

  /**
   * Instantiates a new serializable ldap attribute.
   *
   * @param ldapAttribute the ldap attribute
   */
  public SerLdapAttr(LdapAttribute ldapAttribute) {
    this.attributeName = ldapAttribute.getName();
    this.attributeValues = ldapAttribute.getBinaryValues();
    this.binary = ldapAttribute.isBinary();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    SerLdapAttr that = (SerLdapAttr) o;
    return binary == that.binary
        && Objects.equals(attributeName.toLowerCase(), that.attributeName.toLowerCase())
        && attributeValues.size() == that.attributeValues.size()
        && attributeValues.stream().allMatch(that::hasValue);
  }

  @Override
  public int hashCode() {
    int hash = 10223;
    int index = 1;
    for (byte[] b : attributeValues) {
      hash = (hash * 113 + Arrays.hashCode(b)) + index++;
    }
    return hash * 113 + Objects.hash(attributeName, binary);
  }

  /**
   * Returns the number of values in this ldap attribute.
   *
   * @return number of values in this ldap attribute
   */
  public int size() {
    return attributeValues.size();
  }

  /**
   * Returns a single string value of this attribute.
   *
   * @return single string attribute value or null if this attribute is empty
   */
  public String getStringValue() {
    if (attributeValues.isEmpty()) {
      return null;
    }
    final byte[] val = attributeValues.iterator().next();
    return binary ? LdapUtils.base64Encode(val) : LdapUtils.utf8Encode(val);
  }


  /**
   * Returns the values of this attribute as strings. Binary data is base64 encoded. The return
   * collection cannot be modified.
   *
   * @return collection of string attribute values
   */
  @ToString.Include
  public Collection<String> getStringValues() {
    return attributeValues.stream()
        .map(v -> {
          if (binary) {
            return LdapUtils.base64Encode(v);
          }
          return LdapUtils.utf8Encode(v, false);
        })
        .toList();
  }

  /**
   * Returns a single byte array value of this attribute.
   *
   * @return single byte array attribute value or null if this attribute is empty
   */
  public byte[] getBinaryValue() {
    return attributeValues.isEmpty() ? null : attributeValues.iterator().next();
  }


  /**
   * Returns the values of this attribute as byte arrays. The return collection cannot be modified.
   *
   * @return collection of string attribute values
   */
  public Collection<byte[]> getBinaryValues() {
    return attributeValues.stream().toList();
  }

  /**
   * Returns whether the supplied value exists in this attribute.
   *
   * @param value to find
   * @return whether value exists
   */
  public boolean hasValue(final byte[] value) {
    return attributeValues.stream().anyMatch(bb -> Arrays.equals(bb, value));
  }


  /**
   * Returns whether the supplied value exists in this attribute.
   *
   * @param value to find
   * @return whether value exists
   */
  public boolean hasValue(String value) {
    return attributeValues.stream().anyMatch(bb -> Arrays.equals(bb, toByteArray(value)));
  }

  private byte[] toByteArray(String value) {
    if (binary) {
      try {
        return LdapUtils.base64Decode(value);
      } catch (IllegalArgumentException e) {
        return null;
      }
    }
    return LdapUtils.utf8Encode(value, false);
  }

}
