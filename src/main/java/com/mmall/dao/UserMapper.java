package com.mmall.dao;

import org.apache.ibatis.annotations.Param;

import com.mmall.pojo.User;

public interface UserMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(User record);

    int insertSelective(User record);

    User selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(User record);

    int updateByPrimaryKey(User record);
    /**
     * 
     * @Title: checkUsername
     * @Description: TODO 校验用户名  返回该用户名在数据库中的个数
     * @param @param username
     * @param @return    
     * @return int    
     * @throws
     */
    int checkUsername(String username);
    /**
     * 
     * @Title: checkEamil
     * @Description: TODO  校验Email  返回该Email在数据库中的个数
     * @param @param email
     * @param @return    
     * @return int    
     * @throws
     */
    int checkEmail(String email);
    /**
     * 
     * @Title: selectLogin
     * @Description: TODO 用户使用用户名和密码进行登录
     * @param @param username
     * @param @param password
     * @param @return    
     * @return User    
     * @throws
     */
    User selectLogin(@Param("username")String username,@Param("password")String password);
    /**
     * 
     * @Title: selectQuestionByUsername
     * @Description: TODO 查询该用户的密保问题
     * @param @param username
     * @param @return    
     * @return String    
     * @throws
     */
    String selectQuestionByUsername(String username);
    /**
     * 
     * @Title: checkAnswer
     * @Description: TODO 校验密保问题的回答 根据这三个参数查询用户个数
     * @param @param username
     * @param @param question
     * @param @param answer
     * @param @return    返回的是用户个数
     * @return int    
     * @throws
     */
    int checkAnswer(@Param("username")String username,@Param("question")String question,@Param("answer")String answer);
    
    int updatePasswordByUsername(@Param("username")String username,@Param("password")String passwordNew);
}