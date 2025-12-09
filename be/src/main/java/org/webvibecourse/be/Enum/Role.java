package org.webvibecourse.be.Enum;

public enum Role {
    STUDENT(0),
    TEACHER(1),
    ADMIN(2),
    SUPERADMIN(3),
    SUPPORT(4);

    private final int code;

    Role(int code){
        this.code = code;
    }

    public int getCode(){
        return code;
    }

    public static Role fromCode(int code){
        for (Role role : values()){
            if(role.getCode() == code)
                return role;
        }
        throw new IllegalArgumentException("Invalid Role Code" +code    );
    }
}
