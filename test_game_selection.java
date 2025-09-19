import javafx.application.Application;
import javafx.stage.Stage;
import com.stardewbombers.client.*;
import java.util.Map;
import java.util.HashMap;

public class test_game_selection extends Application {
    public static void main(String[] args) {
        System.out.println("测试游戏选择功能...");
        launch(args);
    }
    
    @Override
    public void start(Stage primaryStage) {
        try {
            // 测试选择的地图和人物
            String selectedMap = "farm_map";
            Map<String, String> selectedCharacters = new HashMap<>();
            selectedCharacters.put("player1", "Abigail");
            selectedCharacters.put("player2", "Alex");
            selectedCharacters.put("player3", "Haley");
            
            System.out.println("测试地图: " + selectedMap);
            System.out.println("测试人物: " + selectedCharacters);
            
            // 启动游戏
            PlayerBombVisualTest game = new PlayerBombVisualTest();
            game.start(primaryStage, selectedMap, selectedCharacters);
            
        } catch (Exception e) {
            System.err.println("测试失败: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
