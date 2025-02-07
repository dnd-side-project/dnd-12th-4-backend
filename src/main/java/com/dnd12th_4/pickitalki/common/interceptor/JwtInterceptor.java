package com.dnd12th_4.pickitalki.common.interceptor;

import com.dnd12th_4.pickitalki.common.token.JwtProvider;
import com.dnd12th_4.pickitalki.presentation.error.TokenErrorCode;
import com.dnd12th_4.pickitalki.presentation.exception.ApiException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
@RequiredArgsConstructor
public class JwtInterceptor implements HandlerInterceptor {

    private final JwtProvider jwtProvider;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if(request.getMethod().equals("OPTIONS")) {
            return true;
        }
        String token = resolveToken(request);

        if (token != null) {
            try {

                jwtProvider.validateToken(token);

            } catch (Exception e) {
                throw new ApiException(TokenErrorCode.INVALID_TOKEN,"boolean preHandle 27번째 에러");
            }
        } else{
            throw new ApiException(TokenErrorCode.AUTHORIZATION_TOKEN_NOT_FOUND,"boolean preHandle 27번째 에러");
        }
        return true;
    }

    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null ) {
            if (bearerToken.startsWith("Bearer ")) return bearerToken.substring(7);
            return bearerToken;

        }
       return null;
    }
}
