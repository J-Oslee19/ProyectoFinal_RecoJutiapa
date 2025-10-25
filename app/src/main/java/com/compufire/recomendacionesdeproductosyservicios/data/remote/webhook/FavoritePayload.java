package com.compufire.recomendacionesdeproductosyservicios.data.remote.webhook;

public class FavoritePayload {
    public String mensaje;

    public static FavoritePayload fromModels(Event evento,
            com.compufire.recomendacionesdeproductosyservicios.data.local.model.Item item,
            com.compufire.recomendacionesdeproductosyservicios.data.local.model.Vendor vendor) {

        FavoritePayload payload = new FavoritePayload();

        if (item == null) {
            payload.mensaje = "No hay información disponible del negocio";
            return payload;
        }

        String categoria = item.categoria != null ? item.categoria.toLowerCase() : "";
        StringBuilder mensaje = new StringBuilder();
        mensaje.append(item.nombre).append("\n\n");

        // Información específica según la categoría principal
        if (categoria.contains("comida") || categoria.contains("restaurante")) {
            mensaje.append("ESTABLECIMIENTO DE COMIDA\n\n")
                  .append("Descripción:\n")
                  .append("Establecimiento gastronómico que ofrece una experiencia culinaria única. ")
                  .append("Local climatizado con ambiente acogedor y música ambiental. ")
                  .append("Atención personalizada y servicio de primera calidad.\n\n")
                  .append("Menú Principal:\n")
                  .append("• Especialidad de la casa\n")
                  .append("• Platos tradicionales\n")
                  .append("• Menú del día\n")
                  .append("• Postres artesanales\n\n")
                  .append("Horario de Atención:\n")
                  .append("• Lunes a Viernes: 7:00 AM - 9:00 PM\n")
                  .append("• Sábado y Domingo: 8:00 AM - 10:00 PM\n\n")
                  .append("Servicios:\n")
                  .append("• Desayunos y almuerzos ejecutivos\n")
                  .append("• Servicio a domicilio\n")
                  .append("• Reservaciones para eventos\n")
                  .append("• Área de parqueo");

        } else if (categoria.contains("servicio")) {
            mensaje.append("ESTABLECIMIENTO DE SERVICIOS\n\n")
                  .append("Descripción:\n")
                  .append("Centro profesional especializado en servicios de calidad. ")
                  .append("Personal altamente capacitado y certificado. ")
                  .append("Instalaciones modernas y equipadas.\n\n")
                  .append("Servicios Disponibles:\n")
                  .append("• Atención personalizada\n")
                  .append("• Asesoría profesional\n")
                  .append("• Diagnóstico gratuito\n")
                  .append("• Garantía del servicio\n\n")
                  .append("Horario de Atención:\n")
                  .append("• Lunes a Viernes: 8:00 AM - 6:00 PM\n")
                  .append("• Sábado: 8:00 AM - 2:00 PM\n\n")
                  .append("Beneficios:\n")
                  .append("• Primera consulta sin costo\n")
                  .append("• Descuentos para clientes frecuentes\n")
                  .append("• Servicio de emergencia\n")
                  .append("• Facilidades de pago");

        } else if (categoria.contains("tecnologia") || categoria.contains("tecno")) {
            mensaje.append("ESTABLECIMIENTO DE TECNOLOGÍA\n\n")
                  .append("Descripción:\n")
                  .append("Tienda especializada en productos y servicios tecnológicos. ")
                  .append("Productos originales con garantía oficial. ")
                  .append("Soporte técnico profesional.\n\n")
                  .append("Productos y Servicios:\n")
                  .append("• Venta de equipos nuevos\n")
                  .append("• Servicio técnico certificado\n")
                  .append("• Accesorios originales\n")
                  .append("• Software y actualizaciones\n\n")
                  .append("Horario de Atención:\n")
                  .append("• Lunes a Sábado: 9:00 AM - 7:00 PM\n")
                  .append("• Domingo: 10:00 AM - 4:00 PM\n\n")
                  .append("Garantías y Servicios:\n")
                  .append("• Garantía oficial de fábrica\n")
                  .append("• Soporte post-venta\n")
                  .append("• Instalación y configuración\n")
                  .append("• Mantenimiento preventivo");
        } else {
            mensaje.append("Información no disponible para esta categoría de negocio.");
        }

        // Agregar información adicional común
        if (item.rating > 0) {
            mensaje.append("\n\nCalificación: ").append(String.format("%.1f/5.0", item.rating));
        }

        String rango = item.precio <= 0 ? "Precios variables" :
                      item.precio < 100 ? "Rango: Económico" :
                      item.precio < 300 ? "Rango: Intermedio" :
                      item.precio < 500 ? "Rango: Alto" : "Rango: Premium";
        mensaje.append("\n").append(rango);

        if (vendor != null) {
            if (vendor.telefono != null && !vendor.telefono.isEmpty()) {
                mensaje.append("\nContacto: ").append(vendor.telefono);
            }
            if (vendor.lat != 0 && vendor.lng != 0) {
                mensaje.append("\nUbicación: ").append(String.format("%.6f", vendor.lat))
                      .append(",").append(String.format("%.6f", vendor.lng));
            }
        }

        payload.mensaje = mensaje.toString();
        return payload;
    }

    public enum Event {
        AGREGADO_A_FAVORITOS,
        ELIMINADO_DE_FAVORITOS
    }
}
