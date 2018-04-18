package com.honeydo5.honeydo;

import com.honeydo5.honeydo.util.InputValidation;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class TestValidation {

    // EMAIL VALIDATION

    @Test
    public void InputValidation_Email_SimpleCorrect() {
        assertThat(InputValidation.validateEmail("honedo@ufl.edu"), is(true));
    }

    @Test
    public void InputValidation_Email_AlternativeCorrect() {
        assertThat(InputValidation.validateEmail("honedo_under.period@cise.ufl.com"), is(true));
    }

    @Test
    public void InputValidation_Email_IncorrectCase1() {
        assertThat(InputValidation.validateEmail("test@ufl"), is(false));
    }

    @Test
    public void InputValidation_Email_IncorrectCase2() {
        assertThat(InputValidation.validateEmail("..@ufl"), is(false));
    }

    @Test
    public void InputValidation_Email_IncorrectCase3() {
        assertThat(InputValidation.validateEmail(""), is(false));
    }

    @Test
    public void InputValidation_Email_IncorrectCase4() {
        assertThat(InputValidation.validateEmail("testufl"), is(false));
    }

    @Test
    public void InputValidation_Email_IncorrectCase5() {
        assertThat(InputValidation.validateEmail("@onlydomain.com"), is(false));
    }

    @Test
    public void InputValidation_Email_IncorrectCase6() {
        assertThat(InputValidation.validateEmail("@onlydomain.com"), is(false));
    }

    @Test
    public void InputValidation_Email_IncorrectCase7() {
        assertThat(InputValidation.validateEmail("test@ulf..com"), is(false));
    }

    @Test
    public void InputValidation_EmptyString_True() {
        assertThat(InputValidation.checkIfEmpty(""), is(true));
    }

    @Test
    public void InputValidation_EmptyString_False() {
        assertThat(InputValidation.checkIfEmpty("t"), is(false));
    }
}
