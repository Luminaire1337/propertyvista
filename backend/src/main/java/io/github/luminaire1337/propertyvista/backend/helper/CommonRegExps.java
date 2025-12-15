package io.github.luminaire1337.propertyvista.backend.helper;

public abstract class CommonRegExps {
    // https://uibakery.io/regex-library/password
    public static final String password = "^(?=.*?[A-Z])(?=.*?[a-z])(?=.*?[0-9])(?=.*?[#?!@$%^&*-]).{8,}$";

    // https://stackoverflow.com/a/23299989
    public static final String phoneNumber = "^\\+[1-9]\\d{1,14}$";

    public static final String image = "^[a-zA-Z0-9/_-]+\\.(jpg|jpeg|png|gif|webp)$";
}
