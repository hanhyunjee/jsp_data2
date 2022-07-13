package sec03.brd02;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;



@WebServlet("/board/*")
public class BoardController extends HttpServlet {

	private static String ARTICLE_IMAGE_REPO = "C:\\board\\article_image";
	BoardService boardService;
	ArticleVO articleVO;
	
	
	public void init(ServletConfig config) throws ServletException {
		boardService = new BoardService();	// BoardService객체를 생성
		articleVO = new ArticleVO();
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
				nextPage = "/board02/listArticles.jsp";
			} else if (action.equals("/listArticles.do")) {		// action값이 /listArticles.do이면 전체글 조회
				articlesList = boardService.listArticles();		// 전체글 조회
				request.setAttribute("articlesList", articlesList);	// 조회된 글 목록을 articlesList로 바인딩 한 후 listArticles.jsp로 포워딩
				nextPage = "/board02/listArticles.jsp";
			} else if (action.equals("/articleForm.do")) {	// action값 /articleForm.do로 요청시 글쓰기 창 나타남
				nextPage = "/board02/articleForm.jsp";
				
			} else if (action.equals("/addArticle.do")) {	//  /addArticle.do로 요청 시 새 글 추가작업
				Map<String, String> articleMap = upload(request,response);  // articleMap : 파일 업로드 기능을 사용하기 위해 upload()로 요청을 전달
				String title = articleMap.get("title");
				String content = articleMap.get("content");
				String imageFileName = articleMap.get("imageFileName");
				
				articleVO.setParentNO(0);		// 새 글의 부모 글 번호를 0으로 설정
				articleVO.setId("hong");		// 새 글 작성자 ID를 hong으로 설정
				articleVO.setTitle(title);
				articleVO.setContent(content);
				articleVO.setImageFileName(imageFileName);
				boardService.addArticle(articleVO);
				nextPage = "/board/listArticles.do";
			}
				
			RequestDispatcher dispatch = request.getRequestDispatcher(nextPage);
			dispatch.forward(request, response);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private Map<String, String> upload(HttpServletRequest request, HttpServletResponse response)
	throws ServletException, IOException {
		Map<String, String> articleMap = new HashMap<String, String>();
		String encoding = "UTF-8";
		File currentDirPath = new File(ARTICLE_IMAGE_REPO);
		DiskFileItemFactory factory = new DiskFileItemFactory();
		factory.setRepository(currentDirPath);
		factory.setSizeThreshold(1024*1024);
		ServletFileUpload upload = new ServletFileUpload(factory);
		try {
			List items = upload.parseRequest(request);
			for (int i=0; i<items.size(); i++) {
				FileItem fileItem = (FileItem) items.get(i);
				if (fileItem.isFormField()) {
					System.out.println(fileItem.getFieldName()+"="
							+ fileItem.getString(encoding));
					articleMap.put(fileItem.getFieldName(), fileItem.getString(encoding));
				} else {
					System.out.println("파라미터이름:"+ fileItem.getFieldName());
					System.out.println("파일이름:"+ fileItem.getName());
					System.out.println("파일크기:"+ fileItem.getSize()+"bytes");
					articleMap.put(fileItem.getFieldName(), fileItem.getName()); // 업로드된 파일의 파일이름을 MAP에 ("imageFileName","업로드파일이름")로 저장합니다.
					
					
				} if (fileItem.getSize()>0) {
					int idx = fileItem.getName().lastIndexOf("\\");
					if (idx == -1) {
						idx = fileItem.getName().lastIndexOf("/");
					}
					
					String fileName = fileItem.getName().substring(idx + 1);
					File uploadFile = new File(currentDirPath + "\\" + fileName);
					fileItem.write(uploadFile);
				}	
			}	
		} catch (Exception e) {
			e.printStackTrace();
		}
		return articleMap;
		
	}
}
