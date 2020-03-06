package gui;

import domein.DomeinController;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class SpelConfigPaneel extends HBox {

    private final DomeinController controller;
    private final HoofdPaneel hoofdPaneel;
    private Stage stage2;
    private final VBox vbx1 = new VBox(), vbx2 = new VBox(), vbxWins = new VBox(), vbxLoad = new VBox(), vbxChallenge = new VBox();
    private VBox vbxItems;
    private final HBox hbxTitels = new HBox(), hbxValues = new HBox();
    private HBox hbxButtons, LoadButtons, ChallengeButtons;
    private final ToggleGroup group = new ToggleGroup();
    private final Region rgnVertLine = new Region(), rgnSpacingLeft = new Region();
    private Region rgButtons;
    private Button btnLoad, btnRemoveLoad, btnLoadChallenge, btnRemoveChallenge, btnRankings;
    private final Label lblSelectLevel = new Label(), foutbericht = new Label();
    private Label lblTableText;
    private final RadioButton rb1 = new RadioButton(), rb2 = new RadioButton(), rb3 = new RadioButton();
    private final Button btnSpeel = new Button(), btnDaagUit = new Button(), btnChallenge = new Button(), btnCancel = new Button();
    private final TableView tblLoad = new TableView(), tblChallenge = new TableView();
    private final TableView tblWins = new TableView();
    private TableView tblPlayers;
    private final ResourceBundle r;
    private boolean isTblLoaded = false, isTblChallengeLoaded = false;
    private List<String[]> namen, namenChallenges, challenges;
    private TextInputDialog dialog;
    private int level, uitgedaagdeSpeler;
    private String titel;
    private ImageView reload;
    private Button btnReload;
    private HBox hbxChallengeTitle;
    private Region rgChallengeTitle;
    private final TextField txtSearch = new TextField();
    private final ObservableList<List<String>> masterPlayers = FXCollections.observableArrayList();

    public SpelConfigPaneel(DomeinController controller, HoofdPaneel hoofdPaneel) {
        this.controller = controller;
        this.hoofdPaneel = hoofdPaneel;
        this.r = controller.getLanguage();
        voegComponentenToe();
        //tables invullen
        setTableItemsGameswon();
        new Thread(() -> {
            setTableItemsLoadgame();
            setTableItemsLoadchallenge();
            showTables();
        }).start();
    }

    private void voegComponentenToe() { //algemene componenten toevoegen
        teksten();
        //LEFT SIDE
        vbx1.setAlignment(Pos.TOP_LEFT);
        vbx1.setPadding(new Insets(10));
        vbx1.setSpacing(10);
        //radiobuttons
        rb1.setId("1");
        rb1.setToggleGroup(group);
        rb1.setSelected(true);
        rb2.setId("2");
        rb2.setToggleGroup(group);
        rb3.setId("3");
        rb3.setToggleGroup(group);
        btnSpeel.setDefaultButton(true);
        btnSpeel.setOnAction(this::start);
        tblWins.setEditable(false);
        tblWins.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        tblWins.setMouseTransparent(true);
        tblWins.setFocusTraversable(false);
        foutbericht.getStyleClass().add("foutbericht");
        foutbericht.setWrapText(true);
        Region rgButtons = new Region();
        HBox.setHgrow(rgButtons, Priority.ALWAYS);
        btnRankings = new Button(r.getString("Rankings"));
        btnRankings.setOnAction(this::showRankings);
        HBox hbxButtons = new HBox(8, btnSpeel, btnDaagUit, rgButtons, btnRankings);

        vbxWins.getChildren().addAll(hbxTitels, hbxValues);
        vbx1.getChildren().addAll(tblWins, rb1, rb2, rb3, rgnSpacingLeft, foutbericht, hbxButtons);
        VBox.setVgrow(rgnSpacingLeft, Priority.ALWAYS);

        //RIGHT SIDE
        LoadButtons = new HBox();
        btnLoad = new Button();
        btnRemoveLoad = new Button();
        tblLoad.setEditable(false);
        tblLoad.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        tblLoad.setOnMouseClicked(this::TblLaadSelected);
        tblLoad.setMinHeight(0);
        tblLoad.setMaxHeight(100);
        btnLoad.setText(r.getString("Load"));
        btnLoad.setOnAction(this::LaadGame);
        btnRemoveLoad.setText(r.getString("Delete"));
        btnRemoveLoad.setOnAction(this::VerwijderGame);
        LoadButtons.setSpacing(10);
        LoadButtons.setPadding(new Insets(5));
        LoadButtons.setAlignment(Pos.CENTER_RIGHT);
        LoadButtons.getChildren().addAll(btnLoad, btnRemoveLoad);
        vbxLoad.getChildren().addAll(new Label(r.getString("LoadGame") + ":"), tblLoad, LoadButtons);
        vbxLoad.setPrefHeight(150);

        ChallengeButtons = new HBox();
        btnLoadChallenge = new Button();
        btnRemoveChallenge = new Button();
        reload = new ImageView(getClass().getResource("/gui/images/reload.png").toExternalForm());
        reload.setFitWidth(15);
        reload.setFitHeight(15);
        btnReload = new Button();
        btnReload.setGraphic(reload);
        btnReload.setOnAction(e -> {
            setTableItemsLoadchallenge();
        });
        tblChallenge.setEditable(false);
        tblChallenge.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        tblChallenge.setOnMouseClicked(this::TblChallengeSelected);
        btnLoadChallenge.setText(r.getString("Load"));
        btnLoadChallenge.setOnAction(this::LaadChallenge);
        btnRemoveChallenge.setText(r.getString("Delete"));
        btnRemoveChallenge.setOnAction(this::VerwijderChallenge);
        ChallengeButtons.setSpacing(10);
        ChallengeButtons.setPadding(new Insets(5));
        ChallengeButtons.setAlignment(Pos.CENTER_RIGHT);
        ChallengeButtons.getChildren().addAll(btnLoadChallenge, btnRemoveChallenge);
        hbxChallengeTitle = new HBox();
        rgChallengeTitle = new Region();
        HBox.setHgrow(rgChallengeTitle, Priority.ALWAYS);
        hbxChallengeTitle.setSpacing(10);
        hbxChallengeTitle.setPadding(new Insets(5));
        hbxChallengeTitle.getChildren().addAll(new Label(r.getString("Challenges") + ":"), rgChallengeTitle, btnReload);
        vbxChallenge.getChildren().addAll(hbxChallengeTitle, tblChallenge, ChallengeButtons);
        vbxChallenge.setPrefHeight(150);

        //vbx2.getChildren().addAll(vbxLoad, vbxChallenge);
        vbx2.setAlignment(Pos.TOP_LEFT);
        vbx2.setPadding(new Insets(10));
        vbx2.setSpacing(10);

        //ADD ALL
        rgnVertLine.setStyle("-fx-border-color: black;"
                + "-fx-border-width: 0.5;"
                + "-fx-border-insets: 10,0, 10, 0;");
        rgnVertLine.setMinWidth(1);

        getChildren().addAll(vbx1);
        vbx1.setMaxWidth(300);
        vbx1.setMinWidth(300);
        setHgrow(vbx2, Priority.ALWAYS);
    }

    public void teksten() {
        lblSelectLevel.setText(String.format("%s:", r.getString("SelectLevel")));
        rb1.setText(r.getString("Easy"));
        rb2.setText(r.getString("Normal"));
        rb3.setText(r.getString("Difficult"));
        btnSpeel.setText(r.getString("StartGame"));
        btnDaagUit.setText(r.getString("Challenge"));
        btnDaagUit.setOnAction(this::DaagUit);

        String[] columnNames = new String[]{r.getString("Easy"), r.getString("Normal"), r.getString("Difficult")};
        tblWins.getColumns().clear();
        for (int i = 0; i < columnNames.length; i++) {
            TableColumn<List<String>, String> column = new TableColumn<>(columnNames[i]);
            final int colIndex = i;
            column.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().get(colIndex)));
            tblWins.getColumns().add(column);
        }

    }

    private void start(ActionEvent event) {
        int level = Integer.parseInt(((RadioButton) group.getSelectedToggle()).getId());
        controller.startNieuwSpel(level);
        hoofdPaneel.speelSpel();
    }

    private void showRankings(ActionEvent event) {
        int level = Integer.parseInt(((RadioButton) group.getSelectedToggle()).getId());
        List<String[]> scoreBoard = controller.geefScores(level);
        if (scoreBoard.isEmpty()) {
            foutbericht.setText(r.getString("ErrNoScores"));
        } else {
            foutbericht.setText("");
            Label lblHeader = new Label();
            HBox headerWon = new HBox();

            lblHeader.setFont(new Font(24));
            lblHeader.setText(r.getString("Rankings"));
            headerWon.getChildren().add(lblHeader);
            headerWon.setAlignment(Pos.CENTER);
            TableView tblRankings = new TableView();
            tblRankings.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

            //static
            if (true) {
                String[] columnNames = new String[]{r.getString("Position"), r.getString("Name"), r.getString("TotalPoints")};
                for (int i = 0; i < columnNames.length; i++) {
                    TableColumn<List<String>, String> column = new TableColumn<>(columnNames[i]);
                    int colIndex = i;
                    column.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().get(colIndex)));
                    tblRankings.getColumns().add(column);
                }
            }
            List<List<String>> data = new ArrayList<>();
            int i = 1;
            int myPlace = -1;
            for (String[] record : scoreBoard) {
                List<String> row = new ArrayList<>();
                row.add("" + i);
                row.add(record[1] + " " + record[2]);
                row.add(record[3]);
                if (Integer.parseInt(record[0]) == controller.geefSpeler().getId()) {
                    myPlace = i - 1;
                }
                data.add(row);
                i++;
            }
            ObservableList<List<String>> inpData = FXCollections.observableArrayList(data);
            tblRankings.setItems(inpData);
            tblRankings.setFixedCellSize(25);
            tblRankings.getSelectionModel().select(myPlace);
            tblRankings.setEditable(false);
            tblRankings.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
            //tblRankings.setMouseTransparent(true);
            tblRankings.setFocusTraversable(false);
            BorderPane bpRankings = new BorderPane();
            bpRankings.setTop(headerWon);
            bpRankings.setCenter(tblRankings);
            Scene scene2 = new Scene(bpRankings, 300, 200);
            Stage stageRankings = new Stage();
            stageRankings.setScene(scene2);
            stageRankings.initModality(Modality.APPLICATION_MODAL);
            stageRankings.initOwner(this.getScene().getWindow());
            stageRankings.showAndWait();
        }
    }

    public final void setTableItemsGameswon() {
        int[][] spellen = controller.geefGewonnenSpellen();

        List<List<String>> data = new ArrayList<>();
        List<String> row1 = new ArrayList<>();
        for (int[] spellen1 : spellen) {
            row1.add("" + spellen1[1]);
        }
        data.add(row1);
        ObservableList<List<String>> inpData = FXCollections.observableArrayList(data);
        tblWins.setItems(inpData);
        tblWins.setFixedCellSize(25);
        //tblWins.prefHeightProperty().bind(Bindings.size(tblWins.getItems()).multiply(tblWins.getFixedCellSize()).add(32));
        tblWins.prefHeightProperty().bind(tblWins.fixedCellSizeProperty().multiply(Bindings.size(tblWins.getItems()).add(1.01)));
        tblWins.minHeightProperty().bind(tblWins.prefHeightProperty());
        tblWins.maxHeightProperty().bind(tblWins.prefHeightProperty());
        if (spellen[0][1] < 20) {
            rb2.setDisable(true);
            rb3.setDisable(true);
            foutbericht.setText(String.format("%s", String.format(r.getString("WinMoreGames"), 20 - spellen[0][1], r.getString("Easy").toLowerCase(), r.getString("Normal").toLowerCase())));
        } else if (spellen[1][1] < 20) {
            rb3.setDisable(true);
            foutbericht.setText(String.format("%s", String.format(r.getString("WinMoreGames"), 20 - spellen[1][1], r.getString("Normal").toLowerCase(), r.getString("Difficult").toLowerCase())));
        }
    }

    public final void setTableItemsLoadgame() {
        //static
        if (!isTblLoaded) {
            String[] columnNames = new String[]{r.getString("Name"), r.getString("LevelCap")};
            for (int i = 0; i < columnNames.length; i++) {
                TableColumn<List<String>, String> column = new TableColumn<>(columnNames[i]);
                int colIndex = i;
                column.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().get(colIndex)));
                tblLoad.getColumns().add(column);
            }
            isTblLoaded = true;
        }
        namen = controller.geefOpgeslagenSpellenNamen();
        List<List<String>> data = new ArrayList<>();
        for (String[] naam : namen) {
            List<String> row = new ArrayList<>();
            row.add("" + naam[1]);
            row.add("" + naam[2]);
            data.add(row);
        }
        ObservableList<List<String>> inpData = FXCollections.observableArrayList(data);
        tblLoad.setItems(inpData);
        tblLoad.setFixedCellSize(25);
        //tblLoad.prefHeightProperty().bind(Bindings.size(tblLoad.getItems()).multiply(tblLoad.getFixedCellSize()).add(32));
        tblLoad.prefHeightProperty().bind(tblLoad.fixedCellSizeProperty().multiply(Bindings.size(tblLoad.getItems()).add(1.01)));

    }

    public final void setTableItemsLoadchallenge() {
        //static
        if (!isTblChallengeLoaded) {
            String[] columnNames = new String[]{r.getString("Name"), r.getString("LevelCap"), r.getString("Player")};
            for (int i = 0; i < columnNames.length; i++) {
                TableColumn<List<String>, String> column = new TableColumn<>(columnNames[i]);
                int colIndex = i;
                column.setCellValueFactory(cellData2 -> new SimpleStringProperty(cellData2.getValue().get(colIndex)));
                tblChallenge.getColumns().add(column);
            }
            isTblChallengeLoaded = true;
        }
        challenges = controller.geefOpgeslagenChallengeNamen();
        List<List<String>> data = new ArrayList<>();
        for (String[] challenge : challenges) {
            List<String> row = new ArrayList<>();
            row.add(challenge[1]);
            row.add(challenge[2]);
            row.add(challenge[4] + " " + challenge[5]);
            data.add(row);
        }
        ObservableList<List<String>> inpData = FXCollections.observableArrayList(data);
        tblChallenge.setItems(inpData);
        tblChallenge.setFixedCellSize(25);
        //tblChallenge.prefHeightProperty().bind(Bindings.size(tblChallenge.getItems()).multiply(tblChallenge.getFixedCellSize()).add(32));
        tblChallenge.prefHeightProperty().bind(tblChallenge.fixedCellSizeProperty().multiply(Bindings.size(tblChallenge.getItems()).add(1.01)));
        tblChallenge.setMinHeight(0);
        tblChallenge.setMaxHeight(100);
    }

    private void showTables() {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                getChildren().removeAll(rgnVertLine, vbx2);
                if (vbx2.getChildren().contains(vbxLoad) && namen.isEmpty()) {
                    vbx2.getChildren().remove(vbxLoad);
                } else if (!vbx2.getChildren().contains(vbxLoad) && !namen.isEmpty()) {
                    vbx2.getChildren().add(vbxLoad);
                }
                if (vbx2.getChildren().contains(vbxChallenge) && challenges.isEmpty()) {
                    vbx2.getChildren().remove(vbxChallenge);
                } else if (!vbx2.getChildren().contains(vbxChallenge) && !challenges.isEmpty()) {
                    vbx2.getChildren().add(vbxChallenge);

                }
                if (vbx2.getChildren().contains(vbxChallenge) || vbx2.getChildren().contains(vbxLoad)) {
                    getChildren().add(1, rgnVertLine);
                    getChildren().add(2, vbx2);
                }
            }
        });
    }

    private void TblLaadSelected(MouseEvent e) {
        if (e.getClickCount() == 2) {
            LaadGame(new ActionEvent());
        }
    }

    private void TblChallengeSelected(MouseEvent e) {
        if (e.getClickCount() == 2) {
            LaadChallenge(new ActionEvent());
        }
    }

    private void LaadGame(ActionEvent e) {
        if (tblLoad.getSelectionModel().getSelectedIndex() > -1) {
            String[] opgeslagenSpel = controller.geefOpgeslagenSpel(namen.get(tblLoad.getSelectionModel().getSelectedIndex())[0]);
            //nieuw spel (level, spelID)
            controller.laadNieuwSpel(Integer.parseInt(opgeslagenSpel[3]), Integer.parseInt(opgeslagenSpel[0]));
            //initialize colors
            controller.setKleuren();
            //code uit database naar CODE array
            List<String> codeList = Arrays.asList(opgeslagenSpel[4].substring(1, opgeslagenSpel[4].length() - 1).split(", "));
            int[] code = controller.databaseCodeToArray(codeList);
            controller.zetCode(code);
            //zetten + correcties
            List<List<Integer>> zetten = new ArrayList<>();
            List<List<Integer>> correcties = new ArrayList<>();
            try {
                //pogingen uit database naar ZETTEN array
                zetten = controller.arraySplit(opgeslagenSpel[5], zetten);
                //correcties uit database naar CORRECTIES array
                correcties = controller.arraySplit(opgeslagenSpel[6], correcties);
            } catch (NullPointerException ex) {
                zetten = null;
                correcties = null;
            }
            hoofdPaneel.laadSpel(zetten, correcties);
        }
    }

    private void LaadChallenge(ActionEvent e) {
        if (tblChallenge.getSelectionModel().getSelectedIndex() > -1) {
            int UitdagerID = Integer.parseInt(challenges.get(tblChallenge.getSelectionModel().getSelectedIndex())[3]);
            String[] opgeslagenSpel = controller.geefOpgeslagenSpel(challenges.get(tblChallenge.getSelectionModel().getSelectedIndex())[0]);
            titel = opgeslagenSpel[2];
            level = Integer.parseInt(opgeslagenSpel[3]);
            //nieuw spel (level, spelID)
            controller.laadNieuwSpel(Integer.parseInt(opgeslagenSpel[3]), Integer.parseInt(opgeslagenSpel[0]));
            //initialize colors
            controller.setKleuren();
            //code uit database naar CODE array
            List<String> codeList = Arrays.asList(opgeslagenSpel[4].substring(1, opgeslagenSpel[4].length() - 1).split(", "));
            int[] code = controller.databaseCodeToArray(codeList);
            controller.zetCode(code);
            //zetten + correcties
            List<List<Integer>> zetten = new ArrayList<>();
            List<List<Integer>> correcties = new ArrayList<>();
            try {
                //pogingen uit database naar ZETTEN array
                zetten = controller.arraySplit(opgeslagenSpel[5], zetten);
                //correcties uit database naar CORRECTIES array
                correcties = controller.arraySplit(opgeslagenSpel[6], correcties);
            } catch (NullPointerException ex) {
                zetten = null;
                correcties = null;
            }
            hoofdPaneel.laadChallenge(zetten, correcties, titel, UitdagerID);
        }
    }

    private void VerwijderGame(ActionEvent e) {
        if (tblLoad.getSelectionModel().getSelectedIndex() > -1) {
            controller.verwijderSpelManueel(Integer.parseInt(namen.get(tblLoad.getSelectionModel().getSelectedIndex())[0]));
            setTableItemsLoadgame();
        }
    }

    private void VerwijderChallenge(ActionEvent e) {
        if (tblChallenge.getSelectionModel().getSelectedIndex() > -1) {
            controller.verwijderChallengeManueel(challenges.get(tblChallenge.getSelectionModel().getSelectedIndex())[1], Integer.parseInt(challenges.get(tblChallenge.getSelectionModel().getSelectedIndex())[3]));
            setTableItemsLoadchallenge();
        }
    }

    public void DaagUit(ActionEvent e) {
        stage2 = new Stage();
        lblTableText = new Label();
        tblPlayers = new TableView();
        vbxItems = new VBox();
        hbxButtons = new HBox();
        rgButtons = new Region();

        //static
        level = Integer.parseInt(((RadioButton) group.getSelectedToggle()).getId());
        lblTableText.setText(r.getString("ChooseChallengePlayer"));
        //table
        tblPlayers.setEditable(false);
        tblPlayers.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        List<String[]> andereSpelers = controller.vindSpelers(level);
        for (String[] andereSpeler : andereSpelers) {
            List<String> row = new ArrayList<>();
            if (andereSpeler[3] != null) {
                row.add("https://app-1524829407.000webhostapp.com/" + andereSpeler[0] + "/profilePic." + andereSpeler[3]);
            } else {
                row.add("/gui/images/profile.png");
            }
            row.add(andereSpeler[1]);
            row.add(andereSpeler[2]);
            masterPlayers.add(row);
        }
        TableColumn<List<String>, ImageView> tc1 = new TableColumn<>("");
        tc1.setCellValueFactory((CellDataFeatures<List<String>, ImageView> param) -> new SimpleObjectProperty<>(new ImageView(new Image(param.getValue().get(0), 17, 17, true, true))));
        tc1.setPrefWidth(23);
        tc1.setMaxWidth(23);
        tc1.setMinWidth(23);

        TableColumn<List<String>, String> tc2 = new TableColumn<>(r.getString("FirstName"));
        tc2.setCellValueFactory((CellDataFeatures<List<String>, String> param) -> new ReadOnlyStringWrapper(param.getValue().get(1)));
        TableColumn<List<String>, String> tc3 = new TableColumn<>(r.getString("LastName"));
        tc3.setCellValueFactory((CellDataFeatures<List<String>, String> param) -> new ReadOnlyStringWrapper(param.getValue().get(2)));
        tblPlayers.getColumns().addAll(tc1, tc2, tc3);
        // tblPlayers.setItems(masterPlayers);
        FilteredList<List<String>> filterPlayers = new FilteredList<>(masterPlayers, s -> true);
        Label lblSearch = new Label("Zoek:");
        HBox hbxSearch = new HBox(lblSearch, txtSearch);
        hbxSearch.setSpacing(10);
        hbxSearch.setAlignment(Pos.BASELINE_LEFT);
        HBox.setHgrow(txtSearch, Priority.ALWAYS);

        txtSearch.textProperty().addListener((observable, oldValue, newValue) -> {
            filterPlayers.setPredicate(s -> {
                // If filter text is empty, display all persons.
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }
                // Compare first name and last name of every person with filter text.
                String lowerCaseFilter = newValue.toLowerCase();
                if (s.get(1).toLowerCase().contains(lowerCaseFilter)) {
                    return true; // Filter matches first name.
                } else if (s.get(2).toLowerCase().contains(lowerCaseFilter)) {
                    return true; // Filter matches last name.
                }
                return false; // Does not match.
            });
        });
        // Wrap the FilteredList in a SortedList. 
        SortedList<List<String>> sortedData = new SortedList<>(filterPlayers);
        // Bind the SortedList comparator to the TableView comparator.
        sortedData.comparatorProperty().bind(tblPlayers.comparatorProperty());
        // Add sorted (and filtered) data to the table.
        tblPlayers.setItems(sortedData);
        tblPlayers.setPlaceholder(new Label(r.getString("ErrNoPlayers")));

        //Buttons
        HBox.setHgrow(rgButtons, Priority.ALWAYS);
        btnCancel.setText(r.getString("Cancel"));
        btnCancel.setOnAction(ev -> {
            stage2.close();
        });
        btnChallenge.setText(r.getString("ChallengePlayer"));
        btnChallenge.setOnAction(ev -> {
            if (tblPlayers.getSelectionModel().getSelectedIndex() > -1) {
                uitgedaagdeSpeler = Integer.parseInt(andereSpelers.get(tblPlayers.getSelectionModel().getSelectedIndex())[0]);
                namenChallenges = controller.geefBestaandeOpgeslagenChallengeNamen();
                dialog = new TextInputDialog();
                dialog.initOwner(this.getScene().getWindow());
                dialog.setTitle(r.getString("NewTitle"));
                dialog.setHeaderText(null);
                dialog.setContentText(r.getString("EnterName"));
                Optional<String> resultSave = dialog.showAndWait();
                resultSave.ifPresent(this::SaveGame);
            }
        });
        btnChallenge.setDefaultButton(true);
        hbxButtons.getChildren().addAll(btnCancel, rgButtons, btnChallenge);
        //ALL
        vbxItems.getChildren().addAll(lblTableText, hbxSearch, tblPlayers, hbxButtons);
        vbxItems.setPadding(new Insets(10));
        vbxItems.setSpacing(10);
        Scene scene2 = new Scene(vbxItems, 300, 300);
        stage2.setScene(scene2);
        stage2.initModality(Modality.APPLICATION_MODAL);
        stage2.initOwner(this.getScene().getWindow());
        stage2.showAndWait();
    }

    private void SaveGame(String event) {
        boolean titelBestaat = false;
        for (String[] naam : namenChallenges) {
            if (naam[1].equals(event)) {
                titelBestaat = true;
            }
        }
        if (titelBestaat) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.initOwner(this.getScene().getWindow());
            alert.setTitle(r.getString("ErrTitleExists"));
            alert.setHeaderText(null);
            alert.setContentText(r.getString("ErrSaveGameExists"));
            alert.showAndWait();
            Optional<String> resultSave = dialog.showAndWait();
            resultSave.ifPresent(this::SaveGame);

        } else {
            stage2.close();
            titel = event;
//start nieuw spel met bepaald level (maakt geheime code)
            controller.startNieuwSpel(level);
//initialize colors
            controller.setKleuren();
            controller.voegUitdagingToe(uitgedaagdeSpeler, titel);
//speel spel
            hoofdPaneel.speelChallenge(titel);
        }
    }
}
