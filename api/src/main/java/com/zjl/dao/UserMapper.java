package com.zjl.dao;

import com.zjl.domain.dbentity.User;
import org.apache.ibatis.annotations.*;

@Mapper
public interface UserMapper {
    @Select("select secret from passwd where uid = #{uid}")
    String getUserSecret(@Param("uid") long uid);

    @Select("select uid from user_profiles u,passwd p where p.uid = u.id and u.email = #{email} " +
            "and p.password = #{passwd}")
    Long login(@Param("email") String email,@Param("passwd") String passwd);

    @Select("select salt from user_profiles u,passwd p where u.id=p.uid and u.email = #{email}")
    String getUserSalt(@Param("email") String email);

    @Insert("insert into user_profiles (email, name) values (#{user.email},#{user.name})")
    @Options(useGeneratedKeys = true,keyProperty = "id",keyColumn = "id")
    long registry(@Param("user") User user);

    @Insert("insert into passwd (uid, password, secret,salt) values (#{uid},#{salt},#{passwd},#{secret})")
    int setPasswd(@Param("uid") Long uid, @Param("salt") String salt,@Param("passwd") String passwd,@Param("secret") String secret);
}
