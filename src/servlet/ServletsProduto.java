package servlet;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import beans.Produto;
import dao.DaoProduto;

@WebServlet("/salvarProduto")
public class ServletsProduto extends HttpServlet {
	private static final long serialVersionUID = 1L;

	private DaoProduto daoProduto = new DaoProduto();

	public ServletsProduto() {
		super();
	}

	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		try {
			String acao = request.getParameter("acao") != null ? request.getParameter("acao"): "listartodos";
			String produto = request.getParameter("produto");
			
			RequestDispatcher view = request
					.getRequestDispatcher("/cadastroProduto.jsp");

			if (acao.equalsIgnoreCase("delete")) {
				daoProduto.delete(produto);
				request.setAttribute("produtos", daoProduto.listarProduto());
				
			} else if (acao.equalsIgnoreCase("editar")) {
				Produto beanCursoJsp = daoProduto.consultar(produto);
				request.setAttribute("produto", beanCursoJsp);

			} else if (acao.equalsIgnoreCase("listartodos")) {
				request.setAttribute("produtos", daoProduto.listarProduto());
			}
			
			request.setAttribute("categorias", daoProduto.listaCategorias());
			view.forward(request, response);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		String acao = request.getParameter("acao");

		if (acao != null && acao.equalsIgnoreCase("reset")) {
			try {
				RequestDispatcher view = request
						.getRequestDispatcher("/cadastroProduto.jsp");
				request.setAttribute("produtos", daoProduto.listarProduto());
				view.forward(request, response);

			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {

			String id = request.getParameter("id");
			String nome = request.getParameter("nome");
			String quantidade = request.getParameter("quantidade");
			String valor = request.getParameter("valor");
			String categoria = request.getParameter("categoria_id");

			Produto produto = new Produto();
			produto.setId(!id.isEmpty() ? Long.parseLong(id) : null);
			produto.setNome(nome);
			produto.setCategoria_id(Long.parseLong(categoria));
			
			if (quantidade != null && !quantidade.isEmpty()) {
				produto.setQuantidade(Double.parseDouble(quantidade));
			}

			if (valor != null && !valor.isEmpty()) {
				//"raplace" - substituir , por .
				String valorParse = valor.replaceAll("\\.", ""); //10500,20
				valorParse = valorParse.replaceAll("\\,", "."); //10500.20
				produto.setValor(Double.parseDouble(valorParse)); 
			}

			try {

				String msg = null;
				boolean podeInserir = true;
				
				if(nome == null || nome.isEmpty()){
					msg = "Nome deve ser informado";
					podeInserir = false;
				
				}else if(quantidade == null || quantidade.isEmpty()){
					msg = "Quantidade de ser informado";
					podeInserir = false;
					
				}else if(valor == null || valor.isEmpty()){
					msg = "Valor de ser informado";
					podeInserir = false;
				}

				else if (id == null || id.isEmpty() && !daoProduto.validarNome(nome)) {// QUANDO
																					// FDOR
																					// PRODUTO
																					// NOVO
					msg = "Produto j� existe com o mesmo nome!";
					podeInserir = false;

				}

				if (msg != null) {
					request.setAttribute("msg", msg);
				}

				else if (id == null || id.isEmpty() && daoProduto.validarNome(nome)
						&& podeInserir) {

					daoProduto.salvar(produto);

				} else if (id != null && !id.isEmpty() && podeInserir) {
					daoProduto.atualizar(produto);
				}

				if (!podeInserir) {
					request.setAttribute("produto", produto);
				}

				RequestDispatcher view = request
						.getRequestDispatcher("/cadastroProduto.jsp");
				request.setAttribute("produtos", daoProduto.listarProduto());
				request.setAttribute("categorias", daoProduto.listaCategorias());
				view.forward(request, response);

			} catch (Exception e) {
				e.printStackTrace();
			}

		}

	}

}
