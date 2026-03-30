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

    /**
     * Формирует текстовую справку для клиента на основе шаблона.
     * Аналог логики создания отчета из методички.
     */
    public void generateClientCertificate(Client client) throws JRException {
        // Формируем строку ФИО согласно заданию из ЛАБ9
        String fullFio = client.getSurname() + " " + client.getName() + " " + client.getPatronymic();

        // Подготовка параметров для отчета
        Map<String, Object> parameters = new HashMap<>();
        parameters.put(PARAM_FIO, fullFio);

        InputStream reportStream = getClass().getResourceAsStream(CLIENT_REPORT_PATH);
        if (reportStream == null) {
            throw new JRException("Файл шаблона отчета не найден: " + CLIENT_REPORT_PATH);
        }

        // Компиляция и заполнение (используем пустой источник данных, так как берем данные из параметров)
        JasperReport jasperReport = JasperCompileManager.compileReport(reportStream);
        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, new JREmptyDataSource());

        // Отображение отчета
        JasperViewer.viewReport(jasperPrint, false);
    }
}