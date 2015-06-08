package servlet;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cnell.util.FileUtil;

/**
 * Servlet implementation class ProbaseServlet
 */
@WebServlet("/SvcServlet")
public class SvcServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public SvcServlet() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	@Override
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		response.setHeader("Content-type", "text/html;charset=UTF-8");
		PrintWriter writer = response.getWriter();
		String resJson = FileUtil
				.readFile("/Users/apple/Documents/workspace/KnowledgeWorks/WebContent/supercode/demoResponse.json");
		writer.write(resJson);
		writer.flush();
		writer.close();
	}

	@Override
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		String vid = request.getParameter("vid");
		String[] answers = request.getParameterValues("answers");
		for (String string : answers) {
			System.out.println(string);
		}
		response.setHeader("Content-type", "text/html;charset=UTF-8");
		PrintWriter writer = response.getWriter();
		writer.write("{\"vid\":\"" + vid + "\",\"status\":\"success\"}");
		writer.flush();
		writer.close();
	}

}
