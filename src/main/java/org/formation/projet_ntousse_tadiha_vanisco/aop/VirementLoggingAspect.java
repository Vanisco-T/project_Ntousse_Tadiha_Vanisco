package org.formation.projet_ntousse_tadiha_vanisco.aop;


import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class VirementLoggingAspect {

    private static final Logger logger = LoggerFactory.getLogger("virement-log");

    @Around("execution(* org.formation.projet_ntousse_tadiha_vanisco.services.VirementService.effectuerVirement(..))")
    public Object logVirement(ProceedingJoinPoint joinPoint) throws Throwable {
        String methodName = joinPoint.getSignature().getName();
        Object[] args = joinPoint.getArgs();

        String source = (String) args[0];
        String destination = (String) args[1];
        Double montant = (Double) args[2];

        logger.info("TENTATIVE VIREMENT - Source: {}, Destination: {}, Montant: {}",
                source, destination, montant);

        long startTime = System.currentTimeMillis();

        try {
            Object result = joinPoint.proceed();
            long endTime = System.currentTimeMillis();
            long duration = endTime - startTime;

            logger.info("VIREMENT RÉUSSI - Source: {}, Destination: {}, Montant: {}, Durée: {}ms",
                    source, destination, montant, duration);
            return result;
        } catch (Exception e) {
            long endTime = System.currentTimeMillis();
            long duration = endTime - startTime;

            logger.error("VIREMENT ÉCHOUÉ - Source: {}, Destination: {}, Montant: {}, Durée: {}ms, Erreur: {}",
                    source, destination, montant, duration, e.getMessage());
            throw e;
        }
    }
}
