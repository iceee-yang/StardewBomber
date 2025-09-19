import com.stardewbombers.farm.*;

public class test_gold_save {
    public static void main(String[] args) {
        System.out.println("测试金币保存...");
        
        try {
            // 创建农场服务
            SimpleFarmService farmService = new SimpleFarmService();
            System.out.println("✓ 农场服务创建成功");
            
            // 添加一些金币
            farmService.addGold("testPlayer", 10);
            int gold = farmService.getGoldCount("testPlayer");
            System.out.println("✓ 添加金币后: " + gold);
            
            // 手动保存数据
            farmService.saveAllData();
            System.out.println("✓ 数据保存完成");
            
            // 创建新的服务实例来测试加载
            SimpleFarmService farmService2 = new SimpleFarmService();
            int loadedGold = farmService2.getGoldCount("testPlayer");
            System.out.println("✓ 加载的金币: " + loadedGold);
            
            if (loadedGold == gold) {
                System.out.println("✓ 金币保存和加载成功！");
            } else {
                System.out.println("❌ 金币保存和加载失败！");
            }
            
        } catch (Exception e) {
            System.err.println("❌ 金币保存测试失败: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
