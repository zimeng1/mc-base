package com.mc.base.redis;

/**
 * 集中redis key管理
 */
public interface RedisKey {

	String UNDER_LINE = "_";

	String MIDDLE_LINE = "-";
	/**
	 * token + token
	 */
	String TOKEN_PREFIX = "token_";

	// 被挤掉线的token + token值
	String TOKEN_OUT_PREFIX = "token_out_";

	// 用户会话 + 用户id
	String SESSION_PREFIX = "session_";

	// 注册验证码 reg+ 手机号
	String REG_PREFIX = "reg_";

	// 登陆验证码 long+ 手机号
	String LOGIN_PREFIX = "login_";

	// 忘记密码验证码 forget+ 手机号
	String FORGET_PREFIX =  "forget_";

	// 重置登陆密码时获取验证码 pwd+ 手机号
	String RESET_PWD_PREFIX = "pwd_";

	// 短信验证码限速前缀
	String RATE_SMS_PREFIX = "rate_";

	// 限速注册验证码 reg+ 手机号
	String RATE_REG_PREFIX = "rate_reg_";

	// 限速登陆验证码 long+ 手机号
	String RATE_LOGIN_PREFIX = "rate_login_";

	// 限速忘记密码验证码 forget+ 手机号
	String RATE_FORGET_PREFIX =  "rate_forget_";

	// 限速绑定账户时获取验证码+ 手机号
	String RATE_BINDING_PREFIX = "rate_bind_";

	// 限速重置pin码时获取验证码+ 手机号
	String RATE_RESET_PIN_PREFIX = "rate_pin_";

	// 限速重置登陆密码时获取验证码+ 手机号
	String RATE_RESET_PWD_PREFIX = "rate_pwd_";

	// 邮箱限速
	String RATE_MAIL_VERIFY_PREFIX = "rate_mail_";

	// 邮箱验证分类
	String MAIL_VERIFY_PREFIX = "mail_";

	// 交易密码错误次数 error_pin_ + 用户id
	String PIN_ERROR_COUNT_PREFIX = "error_pin_";

	// 交易密码错误次数 error_pwd_ + 用户id
	String LOGIN_PWD_ERROR_COUNT_PREFIX = "error_pwd_";

	// 冻结用户交易(pin 连续错误次数超出) hold_pin_ + uid
	String HOLD_USER_PIN_PREFIX = "hold_pin_";

	// 冻结登陆(登陆密码 连续错误次数超出) hold_login_ + uid
	String HOLD_USER_LOGIN_PREFIX = "hold_login_";
	/**
	 * 数据字典前缀
	 */
	String DICTIONARY_PREFIX = "dic_";
	/**
	 * 人脸认证次数
	 */
	String FACE_AUTH = "face_auth_";
}
