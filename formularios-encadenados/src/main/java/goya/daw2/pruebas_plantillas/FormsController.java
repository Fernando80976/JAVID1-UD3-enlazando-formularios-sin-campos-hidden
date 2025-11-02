package goya.daw2.pruebas_plantillas;

import java.util.ArrayList;
import java.net.URLEncoder;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;

@Controller
public class FormsController {

    static final String[] SIGNOS = { "", "Aries", "Tauro", "Géminis", "Cáncer", "Leo", "Virgo", "Libra", "Escorpio",
            "Sagitario", "Capricornio", "Acuario", "Piscis" };
    static final String[] AFICCIONES = { "Deportes", "Juerga", "Lectura", "Relaciones sociales" };

    @GetMapping("/")
    String etapa1() {
        return "etapa1";
    }

    @PostMapping("/")
    String procesaEtapaX(
            @RequestParam(name = "numEtapa") Integer numEtapa,
            @RequestParam(name = "nombre", required = false) String nombre,
            @RequestParam(name = "signo", required = false) String signo,
            @RequestParam(name = "aficciones", required = false) String aficciones,

            @CookieValue(name = "nombre", required = false) String cNombre,
            @CookieValue(name = "signo", required = false) String cSigno,
            @CookieValue(name = "aficciones", required = false) String cAficciones,

            Model modelo, HttpServletResponse response) {

        // nombre
        if (nombre != null) {
            Cookie ck = new Cookie("nombre", nombre);
            ck.setMaxAge(300);
            response.addCookie(ck);
        } else {
            nombre = cNombre;
        }

        // signo
        if (signo != null) {
            Cookie ck = new Cookie("signo", signo);
            ck.setMaxAge(300);
            response.addCookie(ck);
        } else {
            signo = cSigno;
        }

        if (aficciones != null) {
            Cookie ck = new Cookie("aficciones", URLEncoder.encode(aficciones, StandardCharsets.UTF_8));
            ck.setMaxAge(300);
            response.addCookie(ck);
        } else if (cAficciones != null) {
            aficciones = URLDecoder.decode(cAficciones, StandardCharsets.UTF_8);
        }

       
        String errores = "";

        if (numEtapa == 1 && (nombre == null || nombre.isBlank())) {
            errores = "Debes poner un nombre no vacío";
        } else if (numEtapa == 1 && (nombre.length() < 3 || nombre.length() > 10)) {
            errores = "La longitud del nombre debe estar entre 3 y 10";
        }

        if (numEtapa == 2 && (signo == null || signo.equals("0"))) {
            errores = "Debes seleccionar un signo";
        }

        if (numEtapa == 3 && (aficciones == null || aficciones.isBlank())) {
            errores = "Debes elegir al menos una aficción, no seas soso/a";
        }

        modelo.addAttribute("signos", SIGNOS);
        modelo.addAttribute("aficciones", AFICCIONES);

        if (!errores.isBlank()) {
            modelo.addAttribute("errores", errores);
            modelo.addAttribute("numEtapa", numEtapa);
            return "etapa" + numEtapa;
        }

        numEtapa++;
        modelo.addAttribute("numEtapa", numEtapa);

        if (numEtapa == 4) {
            ArrayList<String> respuestas = new ArrayList<>();
            respuestas.add(nombre);
            respuestas.add(SIGNOS[Integer.parseInt(signo)]);
            respuestas.add(aficciones);
            modelo.addAttribute("respuestas", respuestas);
        }

        return "etapa" + numEtapa;
    }
}
