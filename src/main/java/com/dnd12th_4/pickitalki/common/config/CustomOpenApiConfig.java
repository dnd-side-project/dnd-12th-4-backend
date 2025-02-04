package com.dnd12th_4.pickitalki.common.config;

import com.dnd12th_4.pickitalki.common.annotation.MemberId;
import io.swagger.v3.oas.models.parameters.Parameter;
import org.springdoc.core.customizers.OperationCustomizer;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;

import java.util.List;
import java.util.stream.Collectors;
import java.util.Arrays;

@Component
public class CustomOpenApiConfig implements OperationCustomizer {

    @Override
    public io.swagger.v3.oas.models.Operation customize(io.swagger.v3.oas.models.Operation operation, HandlerMethod handlerMethod) {
        if (handlerMethod == null || handlerMethod.getMethodParameters() == null) {
            return operation;
        }

        // 🔹 MethodParameter[] 배열을 Stream으로 변환하여 `@MemberId` 확인
        boolean hasMemberId = Arrays.stream(handlerMethod.getMethodParameters())
                .anyMatch(param -> param.hasParameterAnnotation(MemberId.class));

        if (hasMemberId) {
            // 🔹 @MemberId가 있는 매개변수를 Swagger 문서에서 제거
            List<Parameter> filteredParameters = operation.getParameters().stream()
                    .filter(param -> !param.getName().equalsIgnoreCase("memberId")) // Swagger에서 `memberId` 제거
                    .collect(Collectors.toList());

            operation.setParameters(filteredParameters);
        }

        return operation;
    }
}