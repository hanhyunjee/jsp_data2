package sec03.brd02;

import java.util.List;

public class BoardService {
	BoardDAO boardDAO;
	
	public BoardService() {
		boardDAO = new BoardDAO();	//생성자 호출 시 BoardDAO객체를 생성
	}
	
	public void addArticle(ArticleVO article) {
		boardDAO.insertNewArticle(article);
	}
	
	public List<ArticleVO> listArticles() {
		List<ArticleVO> articlesList = boardDAO.selectAllArticles();
		return articlesList;
	}
}
