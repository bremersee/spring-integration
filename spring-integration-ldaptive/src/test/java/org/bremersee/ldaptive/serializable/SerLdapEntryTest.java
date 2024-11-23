package org.bremersee.ldaptive.serializable;

import java.util.Map;
import org.assertj.core.api.SoftAssertions;
import org.assertj.core.api.junit.jupiter.SoftAssertionsExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.ldaptive.LdapAttribute;
import org.ldaptive.LdapEntry;

/**
 * The serializable ldap entry test.
 */
@ExtendWith({SoftAssertionsExtension.class})
class SerLdapEntryTest {

  /**
   * Gets dn.
   *
   * @param softly the softly
   */
  @Test
  void getDn(SoftAssertions softly) {
    SerLdapEntry e0 = new SerLdapEntry(null);
    SerLdapEntry e1 = new SerLdapEntry(null);
    softly
        .assertThat(e0)
        .isEqualTo(e1);
    softly
        .assertThat(e0.hashCode())
        .isEqualTo(e1.hashCode());
    softly
        .assertThat(e0.toString())
        .contains("null");
    softly
        .assertThat(e0.getDn())
        .isNull();

    LdapEntry le0 = new LdapEntry();
    le0.setDn("dc=junit");
    e0 = new SerLdapEntry(le0);
    e1 = new SerLdapEntry(le0);
    softly
        .assertThat(e0)
        .isEqualTo(e1);
    softly
        .assertThat(e0.hashCode())
        .isEqualTo(e1.hashCode());
    softly
        .assertThat(e0.toString())
        .contains("dc=junit");
    softly
        .assertThat(e0.getDn())
        .isEqualTo("dc=junit");
  }

  /**
   * Gets attributes.
   *
   * @param softly the softly
   */
  @Test
  void getAttributes(SoftAssertions softly) {
    SerLdapEntry e0 = new SerLdapEntry(null);
    softly
        .assertThat(e0.getAttributes())
        .isEmpty();

    LdapAttribute la0 = new LdapAttribute("say", " Hello world!");
    LdapEntry le0 = new LdapEntry();
    le0.setDn("dc=junit");
    le0.addAttributes(la0);
    e0 = new SerLdapEntry(le0);
    softly
        .assertThat(e0.getAttributes())
        .containsAllEntriesOf(Map.of("say", new SerLdapAttr(la0)));
  }

}