/*
 * ENUMERADOR
 * Solo se usa como lista fija de opciones
 * TODO: otra forma de crear admins
 */

package base;

public enum Rol {
    MEDICO,
    PACIENTE,
    // Por el momento Los administradores los doy de alta directamente en la DB
    ADMIN
}
