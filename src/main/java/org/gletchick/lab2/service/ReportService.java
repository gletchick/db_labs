package org.gletchick.lab2.service;

import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.view.JasperViewer;
import org.gletchick.lab2.model.Client;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class ReportService {

    private final static String CLIENT_REPORT_PATH = "/reports/client_certificate.jrxml";
    private final static String PARAM_FIO = "p_fio";

    public void generateClientCertificate(Client client) throws JRException {
        String fullFio = client.getSurname() + " " + client.getName() + " " + client.getPatronymic();

        Map<String, Object> parameters = new HashMap<>();
        parameters.put(PARAM_FIO, fullFio);

        InputStream reportStream = getClass().getResourceAsStream(CLIENT_REPORT_PATH);
        if (reportStream == null) {
            throw new JRException("Файл шаблона отчета не найден: " + CLIENT_REPORT_PATH);
        }

        JasperReport jasperReport = JasperCompileManager.compileReport(reportStream);
        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, new JREmptyDataSource());

        JasperViewer.viewReport(jasperPrint, false);
    }
}