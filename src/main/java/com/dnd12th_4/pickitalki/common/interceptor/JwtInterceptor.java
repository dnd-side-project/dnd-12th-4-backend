package com.dnd12th_4.pickitalki.common.interceptor;

import com.dnd12th_4.pickitalki.common.token.JwtProvider;
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
        String token = resolveToken(request);

        if (token != null) {
            try{
                jwtProvider.validateToken(token);
                Long userId = jwtProvider.getUserIdFromToken(token);

                request.setAttribute("memberId",userId);
            }catch(Exception e){
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED,"유효하지 않는 토큰입니다");
                return false;
            }
        }else{
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "토큰이 없습니다.");
            return false;
        }
        return true;
    }

    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken!=null && bearerToken.startsWith("Bearer ")){
            return bearerToken.substring(7);
        }
        return null;
    }
}
