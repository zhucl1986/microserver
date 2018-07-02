package cn.com.agree.evs.interceptor;
/**
 * @subject 控制器的拦截器
 * @author zhucl
 * @date 2018-06-26 09:42:16
 * @version v1.0 
 */

import java.lang.reflect.Method;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import cn.com.agree.evs.common.DataCache;
import cn.com.agree.evs.tool.XmlParserTool;

@Aspect
@Component
public class ControllerInterceptor {
	private static Logger logger = LoggerFactory.getLogger(ControllerInterceptor.class);
	@Autowired
	private XmlParserTool xmlParserTool;
	
	@Pointcut("execution(* cn.com.agree.evs.controller..*(..)))")
	public void controllerMethodPointcut() {
		logger.info("拦截器执行开始");
	}

	@Around("controllerMethodPointcut()") 
	public String Interceptor(ProceedingJoinPoint pjp) {
		long beginTime = System.currentTimeMillis();
		MethodSignature signature = (MethodSignature) pjp.getSignature();
		// 获取被拦截的方法
		Method method = signature.getMethod(); 
		// 获取被拦截的方法名
		String methodName = method.getName(); 
		Object[] args = pjp.getArgs();
		for (Object arg : args) {
			logger.info("方法 [ " + methodName + " ]");
			//logger.info("参数 [ " + arg + " ]");
			if (arg instanceof String) {
				//处理请求参数
				String reqXmlStr = (String) arg;
				xmlParserTool.parse(methodName, reqXmlStr);
			}
			logger.info("缓冲池参数 [" + DataCache.dataMap.get() + "]");
		}
		Object result = null;
		try {
			// 一切正常的情况下，继续执行被拦截的方法
			result = pjp.proceed();	
		} catch (Throwable e) {
			logger.info("exception: ", e);
		}
		long entTime = System.currentTimeMillis();
		logger.info("本次交易耗时为：" + (entTime - beginTime) + "ms");
		return result.toString();
	}
}
