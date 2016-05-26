package com.bryanreinero.lambda;

import com.bryanreinero.firehose.metrics.Format;
import com.bryanreinero.firehose.metrics.SampleSet;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by brein on 5/25/2016.
 */
public class StatisticsServlet extends HttpServlet {

    SampleSet samples;

    @Override
    public void init(ServletConfig config) throws ServletException {
        samples = (SampleSet) config.getServletContext().getAttribute( "stats" );
    }

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        resp.setContentType("text/plain");
        resp.getWriter().println(Format.report( samples ) );
    }
}

