package io.github.config.filter;

import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.annotation.Order;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.annotation.WebInitParam;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 防止XSS攻击的过滤器(@Order标识执行顺序 值越小越先执行)
 *
 * @author Created by 思伟 on 2019/12/25
 */
@Order(2)
@WebFilter(initParams = {
        @WebInitParam(name = XssFilter.PARAM_NAME_EXCLUSIONS, value = "http://localhost,http://127.0.0.1,")
}, filterName = "xssFilter", urlPatterns = {"/*"})
@Slf4j
public class XssFilter implements Filter {

    /**
     * 参数名
     */
    public static final String PARAM_NAME_EXCLUSIONS = "exclusions";

    /**
     * 排除链接
     */
    public List<String> excludes = Lists.newArrayList();

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        Assert.notNull(filterConfig, "FilterConfig must not be null");
        log.info("WebFilter->[{}] init success...", filterConfig.getFilterName());
        {
            // 普通代码块
            String temp = filterConfig.getInitParameter(PARAM_NAME_EXCLUSIONS);
            String[] url = StringUtils.split(temp, ",");
            for (int i = 0; url != null && i < url.length; i++) {
                excludes.add(url[i]);
            }
        }
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        if (request instanceof HttpServletRequest && response instanceof HttpServletResponse) {
            HttpServletRequest req = (HttpServletRequest) request;
            HttpServletResponse resp = (HttpServletResponse) response;
            if (handleExcludeURL(req, resp)) {
                chain.doFilter(request, response);
                return;
            }
            XssHttpServletRequestWrapper xssRequest = new XssHttpServletRequestWrapper((HttpServletRequest) request);
            chain.doFilter(xssRequest, response);
        } else {
            chain.doFilter(request, response);
        }
    }

    /**
     * 当前请求是否是排除的链接
     *
     * @return boolean
     */
    private boolean handleExcludeURL(HttpServletRequest request, HttpServletResponse response) {
        Assert.notNull(request, "HttpServletRequest must not be null");
        Assert.notNull(response, "HttpServletResponse must not be null");
        if (ObjectUtils.isEmpty(excludes)) {
            return false;
        }
        String requestURI = request.getRequestURI();
        String url = request.getServletPath();
        for (String pattern : excludes) {
            Pattern p = Pattern.compile("^" + pattern);
            Matcher m = p.matcher(url);
            if (m.find()) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void destroy() {
    }

}