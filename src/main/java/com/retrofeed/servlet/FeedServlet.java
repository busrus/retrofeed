package com.retrofeed.servlet;

import com.retrofeed.service.MastodonService;
import com.retrofeed.config.ConfigurationService;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@WebServlet("/feed")
public class FeedServlet extends HttpServlet {
    private MastodonService mastodonService;
    
    @Override
    public void init() throws ServletException {
        ConfigurationService config = (ConfigurationService) 
            getServletContext().getAttribute("config");
            
        if (config == null || !config.isConfigured()) {
            throw new ServletException("RetroFeed not properly configured!");
        }
        
        mastodonService = new MastodonService();
        mastodonService.configure(
            config.getMastodonInstance(),
            config.getMastodonToken()
        );
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
            
        String userAgent = request.getHeader("User-Agent");
        boolean isCurl = userAgent != null && userAgent.toLowerCase().contains("curl");
        
        if (isCurl) {
            serveTextContent(response);
        } else {
            serveHtmlContent(response);
        }
    }
    
    private void serveTextContent(HttpServletResponse response) throws IOException {
        response.setContentType("text/plain; charset=UTF-8");
        
        try (PrintWriter out = response.getWriter()) {
            out.println("=== RetroFeed ===");
            
            List<String> feed = mastodonService.getFeed();
            for (String post : feed) {
                out.println("--------------");
                out.println(post);
            }
            
            out.println("==============");
        }
    }
    
    private void serveHtmlContent(HttpServletResponse response) throws IOException {
        response.setContentType("text/html; charset=UTF-8");
        
        try (PrintWriter out = response.getWriter()) {
            out.println("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 2.0//EN\">");
            out.println("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\">");
            out.println("<html>");
            out.println("<head><title>RetroFeed</title></head>");
            out.println("<body>");
            
            out.println("<p><b>=== RetroFeed ===</b></p>");
            
            List<String> feed = mastodonService.getFeed();
            for (String post : feed) {
                out.println("<p>--------------</p>");
                out.println("<p>" + post.replace("\n", "<br>") + "</p>");
            }
            
            out.println("<p>==============</p>");
            out.println("</body></html>");
        }
    }
}
