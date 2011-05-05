package ru.neoflex.aspectj.logging;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.aspectj.lang.reflect.CodeSignature;

import ru.neoflex.aspectj.apache.commons.lang.StringUtils;

public aspect Logging issingleton() {

	// pointcut throwsRemoteException(): within(com.company.lib..*)
	// && execution(* *(..) throws RuntimeException+);
	//
	// Object around(): throwsRemoteException() {
	// try {
	// return proceed();
	// } catch (RuntimeException re) {
	// re.printStackTrace(System.err);
	// throw re;
	// }
	// }

	pointcut loggedClassMethodExecution() : execution(!@NoLog * @Logged *.* (..));

	pointcut annotatedLoggedMethodExecution() : execution(@Logged * *.* (..));

	// Object around(): annotatedLoggedMethodExecution() ||
	// loggedClassMethodExecution() {
	// String className =
	// thisJoinPoint.getSignature().getDeclaringType().getName();
	// Logger logger = Logger.getLogger(className);
	// CodeSignature codeSignature = (CodeSignature)
	// thisJoinPoint.getSignature();
	//
	// System.out.println("Entering: " + thisJoinPoint.getSignature().getName()
	// + " Args: "
	// + LoggingLogic.printInputParams(thisJoinPoint, (CodeSignature)
	// thisJoinPoint.getSignature()));
	//
	// if (logger.isLoggable(Level.FINER)) {
	// logger.entering(className, codeSignature.getName(), "Args: "
	// + LoggingLogic.printInputParams(thisJoinPoint, codeSignature));
	// }
	//
	// try {
	// Object result = proceed();
	//
	// if (logger.isLoggable(Level.FINER)) {
	// logger.exiting(className, codeSignature.getName(), result != null ?
	// "Result = "
	// + LoggingLogic.forLog(result) : "");
	// }
	//
	// System.out.println("Exiting: " + thisJoinPoint.getSignature().getName()
	// + (result != null ? "Result = " + LoggingLogic.forLog(result) : ""));
	//
	// return result;
	// } catch (RuntimeException e) {
	// System.out.println(e.getClass().getSimpleName() + " occured: " +
	// e.getMessage()
	// + "\nMethod arguments were: " +
	// LoggingLogic.printInputParams(thisJoinPoint, codeSignature));
	//
	// logger.logp(Level.SEVERE, className, codeSignature.getName(),
	// e.getClass().getSimpleName() + " occured: "
	// + e.getMessage() + "\nMethod arguments were: "
	// + LoggingLogic.printInputParams(thisJoinPoint, codeSignature));
	// logger.throwing(className, codeSignature.getName(),
	// e);
	// throw e;
	// }
	// }

	before(): loggedClassMethodExecution() || annotatedLoggedMethodExecution() {
		String className = thisJoinPoint.getSignature().getDeclaringType()
				.getName();
		Logger logger = Logger.getLogger(className);

		try {
			 System.out.println("Entering: " +
			 thisJoinPoint.getSignature().getName() + " Args: "
			 + LoggingLogic.printInputParams(thisJoinPoint, (CodeSignature)
			 thisJoinPoint.getSignature()));

			if (logger.isLoggable(Level.FINER)) {
				CodeSignature codeSignature = (CodeSignature) thisJoinPoint
						.getSignature();

				String argsStr = LoggingLogic.printInputParams(thisJoinPoint,
						codeSignature);
				logger.entering(className, codeSignature.getName(), StringUtils
						.isNotBlank(argsStr) ? "Args: " + argsStr : "");
			}
		} catch (Exception e) {
			logger.logp(Level.SEVERE, className, thisJoinPoint.getSignature()
					.getName(), "\nCannot form ENTERING log record.", e);
		}

	}

	after() returning (Object result)
     : loggedClassMethodExecution() || annotatedLoggedMethodExecution() {
		String className = thisJoinPoint.getSignature().getDeclaringType()
				.getName();
		Logger logger = Logger.getLogger(className);

		try {
			if (logger.isLoggable(Level.FINER)) {
				CodeSignature codeSignature = (CodeSignature) thisJoinPoint
						.getSignature();
				if (result != null) {
					logger.exiting(className, codeSignature.getName(),
							"Result = " + LoggingLogic.forLog(result));
				} else {
					logger.exiting(className, codeSignature.getName());
				}
			}

			// System.out.println("Exiting: " +
			// thisJoinPoint.getSignature().getName()
			// + (result != null ? "Result = " + LoggingLogic.forLog(result) :
			// ""));

		} catch (Exception e) {
			logger.logp(Level.SEVERE, className, thisJoinPoint.getSignature()
					.getName(), "\nCannot form EXITING log record.", e);
		}

	}

	after() throwing (Throwable e)
     : loggedClassMethodExecution() || annotatedLoggedMethodExecution(){
		String className = thisJoinPoint.getSignature().getDeclaringType()
				.getName();
		Logger logger = Logger.getLogger(className);

		CodeSignature codeSignature = (CodeSignature) thisJoinPoint
				.getSignature();

		try {
			// System.out.println(e.getClass().getSimpleName() + " occured: " +
			// e.getMessage() + "\nMethod arguments
			// were: "
			// + LoggingLogic.printInputParams(thisJoinPoint, codeSignature));

			String argsStr = LoggingLogic.printInputParams(thisJoinPoint,
					codeSignature);

			logger.logp(
					Level.SEVERE,
					className,
					codeSignature.getName(),
					e.getClass().getSimpleName()
							+ " occured: "
							+ e.getMessage()
							+ (StringUtils.isNotBlank(argsStr) ? "\nMethod arguments were: "
									+ argsStr
									: "\nMethod has no arguments."));
         logger.throwing(className,
					codeSignature.getName(), e);
		} catch (Exception ex) {
			logger.logp(Level.SEVERE, className, codeSignature.getName(),
					"\nCannot form THROWING log record.", ex);
		}

	}

}
