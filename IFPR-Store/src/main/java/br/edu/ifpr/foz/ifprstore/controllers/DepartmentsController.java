package br.edu.ifpr.foz.ifprstore.controllers;

import br.edu.ifpr.foz.ifprstore.models.Department;
import br.edu.ifpr.foz.ifprstore.repositories.DepartmentRepository;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

@WebServlet(urlPatterns = {"/departments"})
public class DepartmentsController extends HttpServlet {

    private DepartmentRepository repository;

    public DepartmentsController() {
        this.repository = new DepartmentRepository();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String action = req.getParameter("action");

        if (action != null && action.equals("list")) {
            List<Department> departments = repository.getAll();
            req.setAttribute("departments", departments);
            RequestDispatcher dispatcher = req.getRequestDispatcher("/departments.jsp");
            dispatcher.forward(req, resp);
        } else if (action != null && action.equals("search")) {
            String name = req.getParameter("name");
            List<Department> departments = repository.findByName(name);
            req.setAttribute("departments", departments);
            RequestDispatcher dispatcher = req.getRequestDispatcher("/departments.jsp");
            dispatcher.forward(req, resp);
        } else if (action != null && action.equals("view")) {
            Integer id = Integer.parseInt(req.getParameter("id"));
            Department department = repository.getById(id);
            req.setAttribute("department", department);
            RequestDispatcher dispatcher = req.getRequestDispatcher("/department-view.jsp");
            dispatcher.forward(req, resp);
        } else if (action != null && action.equals("withoutSellers")) {
            List<Department> departments = repository.getDepartmentsWithoutSellers();
            req.setAttribute("departments", departments);
            RequestDispatcher dispatcher = req.getRequestDispatcher("/departments.jsp");
            dispatcher.forward(req, resp);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String action = req.getParameter("action");

        if (action != null && action.equals("create")) {
            String name = req.getParameter("name");
            Department department = new Department();
            department.setName(name);
            repository.insert(department);
            resp.sendRedirect("departments?action=list");
        } else if (action != null && action.equals("update")) {
            Integer id = Integer.parseInt(req.getParameter("id"));
            String name = req.getParameter("name");
            Department department = repository.getById(id);
            department.setName(name);
            repository.update(department);
            resp.sendRedirect("departments?action=list");
        } else if (action != null && action.equals("delete")) {
            Integer id = Integer.parseInt(req.getParameter("id"));
            repository.delete(id);
            resp.sendRedirect("departments?action=list");
        }
    }
}
