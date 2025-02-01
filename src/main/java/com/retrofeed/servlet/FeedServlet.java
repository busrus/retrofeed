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
            
        response.setContentType("text/html; charset=UTF-8");
        
        try (PrintWriter out = response.getWriter()) {
            out.println("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 2.0//EN\">");
            out.println("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\">");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>RetroFeed</title>");
            out.println("</head>");
            out.println("<body>");
            
            out.println("<p><b>=== RetroFeed ===</b></p>");
            
            if (!mastodonService.isConfigured()) {
                out.println("<p>Error: Service not configured<br>");
                out.println("Status: ERROR</p>");
                return;
            }
            
            try {
                List<String> feed = mastodonService.getFeed();
                
                for (String post : feed) {
                    out.println("<p>--------------</p>");
                    // Replace newlines with <br> tags
                    String formattedPost = post.replace("\n", "<br>");
                    out.println("<p>" + formattedPost + "</p>");
                }
                
            } catch (IOException e) {
                out.println("<p>Error: Unable to fetch feed<br>");
                out.println("Status: ERROR</p>");
                e.printStackTrace();
            }
            
            out.println("<p>==============</p>");
            out.println("</body>");
            out.println("</html>");
        }
    }
}
