package sec03.brd01;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


//@WebServlet("/board/*")
public class BoardController extends HttpServlet {
	BoardService boardService;
	ArticleVO articleVO;

	
	public void init() throws ServletException {
		boardService = new BoardService();	// BoardService객체를 생성
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doHandle(request,response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doHandle(request,response);
	}
	
	public void doHandle(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String nextPage = "";
		request.setCharacterEncoding("UTF-8");
		response.setContentType("text/html; charset=UTF-8");
		String action = request.getPathInfo();
		System.out.println("action:"+action);
		try {
			List<ArticleVO> articlesList = new ArrayList<ArticleVO>();
			if (action == null) {
				articlesList = boardService.listArticles();
				request.setAttribute("articlesList", articlesList);
				nextPage = "/board01/listArticles.jsp";
			} else if (action.equals("/listArticles.do")) {		// action값이 /listArticles.do이면 전체글 조회
				articlesList = boardService.listArticles();		// 전체글 조회
				request.setAttribute("articlesList", articlesList);	// 조회된 글 목록을 articlesList로 바인딩 한 후 listArticles.jsp로 포워딩
				nextPage = "/board01/listArticles.jsp";
			}
			RequestDispatcher dispatch = request.getRequestDispatcher(nextPage);
			dispatch.forward(request, response);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
