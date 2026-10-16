package org.iesalixar.daw2.carlosmarin.servlets;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.iesalixar.daw2.carlosmarin.dao.ProvinceDAO;
import org.iesalixar.daw2.carlosmarin.dao.ProvinceDAOImpl;
import org.iesalixar.daw2.carlosmarin.dao.RegionDAO;
import org.iesalixar.daw2.carlosmarin.dao.RegionDAOImpl;
import org.iesalixar.daw2.carlosmarin.entity.Province;
import org.iesalixar.daw2.carlosmarin.entity.Region;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

/**
 * Servlet que gestiona el CRUD de la entidad {@link Province} a través de la
 * URL {@code /provinces}.
 * <p>
 * Utiliza {@link ProvinceDAO} para acceder a las provincias y {@link RegionDAO}
 * para cargar las Comunidades Autónomas que se muestran en el desplegable del
 * formulario. Las validaciones se hacen en el servlet y los mensajes de error
 * se envían a la vista mediante una clave de internacionalización
 * ({@code errorKey}), que la JSP traduce con {@code <fmt:message>}.
 * </p>
 */
@WebServlet("/provinces")
public class ProvinceServlet extends HttpServlet {

    // Logger para registrar entradas, salidas y errores
    private static final Logger logger = LoggerFactory.getLogger(ProvinceServlet.class);

    /** Vista con el listado de provincias. */
    private static final String VIEW_LIST = "provinces.jsp";

    /** Vista con el formulario de creación/edición. */
    private static final String VIEW_FORM = "provinces-form.jsp";

    /** Longitud máxima del código (columna VARCHAR(10)). */
    private static final int MAX_CODE_LENGTH = 10;

    /** Longitud máxima del nombre (columna VARCHAR(100)). */
    private static final int MAX_NAME_LENGTH = 100;

    // DAO para gestionar las provincias en la base de datos
    private ProvinceDAO provinceDAO;

    // DAO para cargar las Comunidades Autónomas del desplegable
    private RegionDAO regionDAO;

    /**
     * Inicializa los DAOs que utiliza el servlet.
     *
     * @throws ServletException si no se pueden crear los DAOs.
     */
    @Override
    public void init() throws ServletException {
        logger.info("Entrando en el método init de ProvinceServlet");
        try {
            provinceDAO = new ProvinceDAOImpl();
            regionDAO = new RegionDAOImpl();
        } catch (Exception e) {
            logger.error("Error al inicializar los DAOs de ProvinceServlet: {}", e.getMessage(), e);
            throw new ServletException("Error al inicializar los DAOs de provincias", e);
        }
        logger.info("Saliendo del método init de ProvinceServlet");
    }

    /**
     * Maneja las solicitudes GET. Según el parámetro {@code action} muestra el
     * formulario de alta ({@code new}), el de edición ({@code edit}) o el
     * listado (por defecto).
     *
     * @param request  Solicitud HTTP.
     * @param response Respuesta HTTP.
     * @throws ServletException en caso de errores en el servlet.
     * @throws IOException      en caso de errores de E/S.
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        logger.info("Entrando en el método doGet de ProvinceServlet");
        response.setContentType("text/html;charset=UTF-8");
        response.setCharacterEncoding("UTF-8");

        String action = request.getParameter("action");
        if (action == null) {
            action = "list";
        }
        logger.info("Acción GET recibida: {}", action);

        try {
            switch (action) {
                case "new":
                    showNewForm(request, response);
                    break;
                case "edit":
                    showEditForm(request, response);
                    break;
                default:
                    listProvinces(request, response);
                    break;
            }
        } catch (SQLException ex) {
            logger.error("Error de base de datos en doGet: {}", ex.getMessage(), ex);
            throw new ServletException(ex);
        }
        logger.info("Saliendo del método doGet de ProvinceServlet");
    }

    /**
     * Maneja las solicitudes POST. Según el parámetro {@code action} inserta,
     * actualiza o elimina una provincia.
     *
     * @param request  Solicitud HTTP.
     * @param response Respuesta HTTP.
     * @throws ServletException en caso de errores en el servlet.
     * @throws IOException      en caso de errores de E/S.
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        logger.info("Entrando en el método doPost de ProvinceServlet");
        // Necesario para leer correctamente tildes y eñes del formulario
        request.setCharacterEncoding("UTF-8");

        String action = request.getParameter("action");
        if (action == null) {
            action = "list";
        }
        logger.info("Acción POST recibida: {}", action);

        try {
            switch (action) {
                case "insert":
                    insertProvince(request, response);
                    break;
                case "update":
                    updateProvince(request, response);
                    break;
                case "delete":
                    deleteProvince(request, response);
                    break;
                default:
                    response.sendRedirect("provinces");
                    break;
            }
        } catch (SQLException ex) {
            logger.error("Error de base de datos en doPost: {}", ex.getMessage(), ex);
            throw new ServletException(ex);
        }
        logger.info("Saliendo del método doPost de ProvinceServlet");
    }

    /**
     * Lista todas las provincias y las pasa a la vista {@code provinces.jsp}.
     *
     * @param request  Solicitud HTTP.
     * @param response Respuesta HTTP.
     * @throws SQLException     en caso de error en la consulta SQL.
     * @throws IOException      en caso de error de E/S.
     * @throws ServletException en caso de error en el servlet.
     */
    private void listProvinces(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, IOException, ServletException {
        logger.info("Entrando en el método listProvinces");
        List<Province> listProvinces = provinceDAO.listAllProvinces();
        request.setAttribute("listProvinces", listProvinces);
        request.getRequestDispatcher(VIEW_LIST).forward(request, response);
        logger.info("Saliendo del método listProvinces");
    }

    /**
     * Muestra el formulario vacío para crear una nueva provincia, cargando las
     * Comunidades Autónomas para el desplegable.
     *
     * @param request  Solicitud HTTP.
     * @param response Respuesta HTTP.
     * @throws SQLException     en caso de error en la consulta SQL.
     * @throws ServletException en caso de error en el servlet.
     * @throws IOException      en caso de error de E/S.
     */
    private void showNewForm(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, ServletException, IOException {
        logger.info("Entrando en el método showNewForm");
        request.setAttribute("listRegions", regionDAO.listAllRegions());
        request.getRequestDispatcher(VIEW_FORM).forward(request, response);
        logger.info("Saliendo del método showNewForm");
    }

    /**
     * Muestra el formulario de edición con los datos de una provincia existente.
     * Si el identificador no es válido o la provincia no existe, vuelve al listado.
     *
     * @param request  Solicitud HTTP.
     * @param response Respuesta HTTP.
     * @throws SQLException     en caso de error en la consulta SQL.
     * @throws ServletException en caso de error en el servlet.
     * @throws IOException      en caso de error de E/S.
     */
    private void showEditForm(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, ServletException, IOException {
        logger.info("Entrando en el método showEditForm");
        int id = parseId(request.getParameter("id"));
        Province province = (id > 0) ? provinceDAO.getProvinceById(id) : null;

        if (province == null) {
            logger.warn("Se ha pedido editar una provincia que no existe (id={})", id);
            response.sendRedirect("provinces");
        } else {
            request.setAttribute("province", province);
            request.setAttribute("listRegions", regionDAO.listAllRegions());
            request.getRequestDispatcher(VIEW_FORM).forward(request, response);
        }
        logger.info("Saliendo del método showEditForm");
    }

    /**
     * Inserta una nueva provincia tras validarla: campos no vacíos, longitudes,
     * Comunidad Autónoma existente y código no repetido. Si hay algún error,
     * vuelve al formulario conservando lo escrito y mostrando el mensaje.
     *
     * @param request  Solicitud HTTP con los datos del formulario.
     * @param response Respuesta HTTP para redirigir o mostrar errores.
     * @throws SQLException     si ocurre un error en la base de datos.
     * @throws IOException      si ocurre un error de entrada/salida.
     * @throws ServletException si ocurre un error en el servlet.
     */
    private void insertProvince(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, IOException, ServletException {
        logger.info("Entrando en el método insertProvince");
        Province province = buildProvinceFromRequest(request, 0);

        String errorKey = validateProvince(province);
        if (errorKey == null && provinceDAO.existsProvinceByCode(province.getCode())) {
            logger.warn("Intento de insertar una provincia con código repetido: {}", province.getCode());
            errorKey = "msg.province-form.error.code-exists";
        }

        if (errorKey != null) {
            forwardToForm(request, response, province, errorKey);
        } else {
            try {
                provinceDAO.insertProvince(province);
                logger.info("Provincia insertada, redirigiendo al listado");
                response.sendRedirect("provinces");
            } catch (SQLException e) {
                if (isIntegrityViolation(e)) {
                    // Por ejemplo, otra petición ha insertado el mismo código a la vez
                    logger.warn("Violación de integridad al insertar la provincia: {}", e.getMessage());
                    forwardToForm(request, response, province, "msg.province-form.error.code-exists");
                } else {
                    throw e;
                }
            }
        }
        logger.info("Saliendo del método insertProvince");
    }

    /**
     * Actualiza una provincia existente tras validarla (mismas reglas que al
     * insertar, pero el código solo puede coincidir con el de la propia provincia).
     *
     * @param request  Solicitud HTTP con los datos del formulario.
     * @param response Respuesta HTTP para redirigir o mostrar errores.
     * @throws SQLException     si ocurre un error en la base de datos.
     * @throws IOException      si ocurre un error de entrada/salida.
     * @throws ServletException si ocurre un error en el servlet.
     */
    private void updateProvince(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, IOException, ServletException {
        logger.info("Entrando en el método updateProvince");
        int id = parseId(request.getParameter("id"));

        if (id <= 0) {
            logger.warn("Se ha intentado actualizar una provincia con id no válido");
            response.sendRedirect("provinces");
        } else {
            Province province = buildProvinceFromRequest(request, id);

            String errorKey = validateProvince(province);
            if (errorKey == null && provinceDAO.existsProvinceByCodeAndNotId(province.getCode(), id)) {
                logger.warn("Intento de actualizar con un código ya usado por otra provincia: {}", province.getCode());
                errorKey = "msg.province-form.error.code-exists";
            }

            if (errorKey != null) {
                forwardToForm(request, response, province, errorKey);
            } else {
                try {
                    provinceDAO.updateProvince(province);
                    logger.info("Provincia actualizada, redirigiendo al listado");
                    response.sendRedirect("provinces");
                } catch (SQLException e) {
                    if (isIntegrityViolation(e)) {
                        logger.warn("Violación de integridad al actualizar la provincia: {}", e.getMessage());
                        forwardToForm(request, response, province, "msg.province-form.error.code-exists");
                    } else {
                        throw e;
                    }
                }
            }
        }
        logger.info("Saliendo del método updateProvince");
    }

    /**
     * Elimina una provincia según su identificador y vuelve al listado.
     *
     * @param request  Solicitud HTTP.
     * @param response Respuesta HTTP.
     * @throws SQLException en caso de error en la consulta SQL.
     * @throws IOException  en caso de error de E/S.
     */
    private void deleteProvince(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, IOException {
        logger.info("Entrando en el método deleteProvince");
        int id = parseId(request.getParameter("id"));

        if (id > 0) {
            provinceDAO.deleteProvince(id);
        } else {
            logger.warn("Se ha intentado eliminar una provincia con id no válido");
        }
        response.sendRedirect("provinces");
        logger.info("Saliendo del método deleteProvince");
    }

    /**
     * Construye una {@link Province} con los datos del formulario. El código se
     * pasa a mayúsculas y se eliminan los espacios sobrantes. La región queda
     * como un objeto con solo el {@code id} elegido en el desplegable.
     *
     * @param request Solicitud HTTP con los datos del formulario.
     * @param id      Identificador de la provincia (0 si es nueva).
     * @return La provincia con los datos recibidos (sin validar).
     */
    private Province buildProvinceFromRequest(HttpServletRequest request, int id) {
        logger.info("Entrando en el método buildProvinceFromRequest");
        String code = trimToEmpty(request.getParameter("code")).toUpperCase();
        String name = trimToEmpty(request.getParameter("name"));

        Region region = new Region();
        region.setId(parseId(request.getParameter("id_region")));

        Province province = new Province();
        province.setId(id);
        province.setCode(code);
        province.setName(name);
        province.setRegion(region);
        logger.info("Saliendo del método buildProvinceFromRequest");
        return province;
    }

    /**
     * Valida los datos de una provincia: campos obligatorios, longitudes máximas
     * y existencia de la Comunidad Autónoma elegida.
     *
     * @param province Provincia a validar.
     * @return La clave del mensaje de error, o {@code null} si todo es correcto.
     * @throws SQLException si falla la consulta de la Comunidad Autónoma.
     */
    private String validateProvince(Province province) throws SQLException {
        logger.info("Entrando en el método validateProvince");
        String errorKey = null;

        if (province.getCode().isEmpty() || province.getName().isEmpty()
                || province.getRegion().getId() <= 0) {
            logger.warn("Validación fallida: hay campos vacíos");
            errorKey = "msg.province-form.error.empty";
        } else if (province.getCode().length() > MAX_CODE_LENGTH
                || province.getName().length() > MAX_NAME_LENGTH) {
            logger.warn("Validación fallida: longitud máxima superada");
            errorKey = "msg.province-form.error.length";
        } else if (regionDAO.getRegionById(province.getRegion().getId()) == null) {
            logger.warn("Validación fallida: la región {} no existe", province.getRegion().getId());
            errorKey = "msg.province-form.error.region";
        }

        logger.info("Saliendo del método validateProvince (error={})", errorKey);
        return errorKey;
    }

    /**
     * Vuelve al formulario mostrando un mensaje de error y conservando los datos
     * escritos. También recarga las Comunidades Autónomas del desplegable.
     *
     * @param request  Solicitud HTTP.
     * @param response Respuesta HTTP.
     * @param province Provincia con los datos introducidos por el usuario.
     * @param errorKey Clave del mensaje de error en los ficheros de mensajes.
     * @throws SQLException     si falla la carga de las Comunidades Autónomas.
     * @throws ServletException en caso de error en el servlet.
     * @throws IOException      en caso de error de E/S.
     */
    private void forwardToForm(HttpServletRequest request, HttpServletResponse response,
                               Province province, String errorKey)
            throws SQLException, ServletException, IOException {
        logger.info("Entrando en el método forwardToForm (errorKey={})", errorKey);
        request.setAttribute("province", province);
        request.setAttribute("errorKey", errorKey);
        request.setAttribute("listRegions", regionDAO.listAllRegions());
        request.getRequestDispatcher(VIEW_FORM).forward(request, response);
        logger.info("Saliendo del método forwardToForm");
    }

    /**
     * Indica si una excepción SQL corresponde a una violación de restricción de
     * integridad (clase de SQLState "23": clave única, clave foránea...).
     *
     * @param e Excepción SQL.
     * @return {@code true} si es una violación de integridad.
     */
    private boolean isIntegrityViolation(SQLException e) {
        String state = e.getSQLState();
        return state != null && state.startsWith("23");
    }

    /**
     * Convierte un texto en un identificador entero.
     *
     * @param value Texto recibido como parámetro.
     * @return El número, o 0 si es nulo o no es un entero válido.
     */
    private int parseId(String value) {
        if (value == null || value.trim().isEmpty()) {
            return 0;
        }
        try {
            return Integer.parseInt(value.trim());
        } catch (NumberFormatException e) {
            logger.warn("Identificador no numérico recibido: {}", value);
            return 0;
        }
    }

    /**
     * Elimina los espacios de los extremos de un texto, tratando {@code null}
     * como cadena vacía.
     *
     * @param value Texto a limpiar.
     * @return El texto sin espacios sobrantes, o "" si era {@code null}.
     */
    private String trimToEmpty(String value) {
        return value == null ? "" : value.trim();
    }
}
