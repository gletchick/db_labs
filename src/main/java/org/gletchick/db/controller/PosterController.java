package org.gletchick.db.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import org.gletchick.db.factory.ServiceFactory;
import org.gletchick.db.model.Spectacle;
import org.gletchick.db.service.SpectacleService;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class PosterController extends BaseController {

    @FXML
    private FlowPane posterFlowPane;
    @FXML
    private TextField titleFilter;
    // Имена должны СТРОГО совпадать с fx:id в FXML
    @FXML
    private ComboBox<String> genreFilter;
    @FXML
    private TextField directorFilter;

    private final SpectacleService spectacleService = ServiceFactory.getInstance().getSpectacleService();

    private ObservableList<Spectacle> masterData = FXCollections.observableArrayList();
    private FilteredList<Spectacle> filteredData;
    @FXML
    private ComboBox<String> ageFilter;
    @FXML
    private ComboBox<String> languageFilter;

    @FXML
    public void initialize() {
        List<Spectacle> all = spectacleService.findAll();
        masterData.addAll(all);
        filteredData = new FilteredList<>(masterData, p -> true);

        setupComboBox(genreFilter, all.stream().map(Spectacle::getGenre).distinct().toList(), "Все жанры");
        setupComboBox(ageFilter, all.stream().map(Spectacle::getAgeRestriction).distinct().toList(), "Любой возраст");
        setupComboBox(languageFilter, all.stream().map(Spectacle::getLanguage).distinct().toList(), "Любой язык");

        // Добавляем слушателя для нового поля названия
        titleFilter.textProperty().addListener((o, old, newVal) -> updateFilter());

        directorFilter.textProperty().addListener((o, old, newVal) -> updateFilter());
        genreFilter.valueProperty().addListener((o, old, newVal) -> updateFilter());
        ageFilter.valueProperty().addListener((o, old, newVal) -> updateFilter());
        languageFilter.valueProperty().addListener((o, old, newVal) -> updateFilter());

        renderPoster();
    }

    private void updateFilter() {
        // Сбор данных из всех полей
        String titleSearch = titleFilter.getText().toLowerCase().trim();
        String directorSearch = directorFilter.getText().toLowerCase().trim();
        String genre = genreFilter.getValue();
        String age = ageFilter.getValue();
        String lang = languageFilter.getValue();

        filteredData.setPredicate(s -> {
            // 1. Проверка названия
            boolean matchesTitle = titleSearch.isEmpty() ||
                    s.getTitle().toLowerCase().contains(titleSearch);

            // 2. Проверка режиссера
            boolean matchesDirector = directorSearch.isEmpty() ||
                    (s.getDirector() != null && s.getDirector().toLowerCase().contains(directorSearch));

            // 3. Остальные фильтры
            boolean matchesGenre = genre.equals("Все жанры") || s.getGenre().equals(genre);
            boolean matchesAge = age.equals("Любой возраст") || s.getAgeRestriction().equals(age);
            boolean matchesLang = lang.equals("Любой язык") || s.getLanguage().equals(lang);

            // Объединяем все условия через "И"
            return matchesTitle && matchesDirector && matchesGenre && matchesAge && matchesLang;
        });

        renderPoster();
    }

    @FXML
    private void handleResetFilters() {
        titleFilter.clear(); // Сбрасываем новое поле
        directorFilter.clear();
        genreFilter.setValue("Все жанры");
        ageFilter.setValue("Любой возраст");
        languageFilter.setValue("Любой язык");
        // renderPoster() вызовется автоматически через слушателей
    }

    // Методы setupComboBox и renderPoster остаются без изменений
    private void setupComboBox(ComboBox<String> cb, List<String> items, String defaultText) {
        cb.getItems().clear();
        cb.getItems().add(defaultText);
        cb.getItems().addAll(items);
        cb.setValue(defaultText);
    }

    private void renderPoster() {
        posterFlowPane.getChildren().clear();
        for (Spectacle spectacle : filteredData) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/poster_item.fxml"));
                VBox card = loader.load();
                PosterItemController itemController = loader.getController();
                itemController.setData(spectacle);
                posterFlowPane.getChildren().add(card);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}