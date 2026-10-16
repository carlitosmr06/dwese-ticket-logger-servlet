package org.iesalixar.daw2.carlosmarin;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@WebServlet("/user")
public class UserServlet extends HttpServlet {
    /**
     * Logger para registrar eventos y mensajes de depuración en el servlet.
     */
    private static final Logger logger =
            LoggerFactory.getLogger(UserServlet.class);

    private String userName = "prof.albertocanca";
    /**
     * Método que maneja las solicitudes HTTP GET.
     * <p>
     * Este método genera una respuesta HTML que muestra un saludo
     personalizado
     * utilizando el valor de la variable {@code userName}.
     * </p>
     *
     * @param request el objeto {@link HttpServletRequest} que contiene la
    solicitud del cliente
     * @param response el objeto {@link HttpServletResponse} que contiene la
    respuesta del servlet
     * @throws ServletException si ocurre un error específico del servlet
     * @throws IOException si ocurre un error de entrada/salida mientras
    se procesa la solicitud
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse
            response) throws ServletException, IOException {
        // Log cuando se entra en el método
        logger.info("Entering the doGet method of UserServlet.");
        // Establecer el atributo "userName" en la solicitud
        request.setAttribute("userName", userName);

                // Redirigir al JSP
                request.getRequestDispatcher("/user.jsp").forward(request,
                        response);
        // Log cuando se sale del método
        logger.info("Exiting the doGet method of UserServlet.");
    }
}

