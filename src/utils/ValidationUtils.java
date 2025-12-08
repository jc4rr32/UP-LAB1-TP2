package utils;


import exceptions.DatosInvalidosException;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class ValidationUtils {

    /**
     * Valida que uno o varios strings no sean nulos ni vacíos.
     * @param fields campos a validar
     * @throws DatosInvalidosException si algún campo está vacío
     */
	//String... varargs (variable arguments) y significa que el método puede recibir cero o más parámetros de tipo String
    public static void validarNoVacio(String... fields) throws DatosInvalidosException {
        for (String s : fields) {
            if (s == null || s.trim().isEmpty()) {
                throw new DatosInvalidosException("Todos los campos son obligatorios.");
            }
        }
    }

    /**
     * Valida el formato básico de un DNI (solo dígitos, entre 7 y 8 caracteres).
     * @param dni cadena a validar
     * @throws DatosInvalidosException si el DNI no cumple el patrón
     */
    public static void validarDni(String dni) throws DatosInvalidosException {
        if (dni == null || !dni.matches("\\d{8}")) {
            throw new DatosInvalidosException("DNI inválido. Debe contener 8 dígitos.");
        }
    }

    /**
     * Valida que la fecha "desde" no sea posterior a la fecha "hasta".
     * @param desde fecha inicial
     * @param hasta fecha final
     * @throws DatosInvalidosException si desde > hasta
     */
    public static void validarRangoFechas(LocalDate desde, LocalDate hasta) throws DatosInvalidosException {
        if (desde == null || hasta == null) {
            throw new DatosInvalidosException("Las fechas no pueden ser nulas.");
        }
        if (desde.isAfter(hasta)) {
            throw new DatosInvalidosException("La fecha inicio no puede ser posterior a la fecha fin.");
        }
    }

    /**
     * Valida que una fecha y hora no esté en el pasado.
     * @param fechaHora fecha y hora a validar
     * @throws DatosInvalidosException si la fecha está en el pasado
     */
    public static void validarFechaNoPasada(LocalDateTime fechaHora) throws DatosInvalidosException {
        if (fechaHora == null) {
            throw new DatosInvalidosException("La fecha y hora son obligatorias.");
        }
        if (fechaHora.isBefore(LocalDateTime.now())) {
            throw new DatosInvalidosException("La fecha y hora no pueden ser una fecha pasada.");
        }
    }
    
    /**
     * Valida que una fecha y hora sea futura.
     * @param fechaHora fecha y hora a validar
     * @throws DatosInvalidosException si la fecha está No es futura
     */
    public static void validarFechaFutura(LocalDateTime fecha) 
            throws DatosInvalidosException {
        if (fecha == null || !fecha.isAfter(LocalDateTime.now())) {
            throw new DatosInvalidosException("La fecha/hora debe ser futura.");
        }
    }
}