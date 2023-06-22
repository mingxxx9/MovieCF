import java.util.HashMap;
import java.util.Map;

public class UserSimilarityCalculator {
    private Map<Integer, Map<Integer, Double>> userRatings;

    public UserSimilarityCalculator(Map<Integer, Map<Integer, Double>> userRatings) {
        this.userRatings = userRatings;
    }

    public double calculateSimilarity(int user1, int user2) {
        Map<Integer, Double> ratings1 = userRatings.get(user1);
        Map<Integer, Double> ratings2 = userRatings.get(user2);

        if (ratings1 == null || ratings2 == null) {
            return 0.0; // 用户不存在或没有评分数据
        }

        double sum = 0.0;
        int commonItems = 0;

        for (Map.Entry<Integer, Double> entry : ratings1.entrySet()) {
            int movieId = entry.getKey();
            double rating1 = entry.getValue();
            Double rating2 = ratings2.get(movieId);

            if (rating2 != null) {
                sum += Math.pow(rating1 - rating2, 2);
                commonItems++;
            }
        }

        if (commonItems == 0) {
            return 0.0; // 没有共同评分的项目
        }

        double similarity = 1.0 / (1.0 + Math.sqrt(sum / commonItems));
        return similarity;
    }

    public static void main(String[] args) {
        // 创建用户评分数据（示例数据）
        Map<Integer, Map<Integer, Double>> userRatings = new HashMap<>();
        Map<Integer, Double> ratings1 = new HashMap<>();
        ratings1.put(1, 4.5);
        ratings1.put(2, 3.0);
        ratings1.put(3, 2.5);
        userRatings.put(1, ratings1);

        Map<Integer, Double> ratings2 = new HashMap<>();
        ratings2.put(2, 4.0);
        ratings2.put(3, 3.5);
        ratings2.put(4, 2.0);
        userRatings.put(2, ratings2);

        Map<Integer, Double> ratings3 = new HashMap<>();
        ratings3.put(1, 3.5);
        ratings3.put(2, 2.0);
        ratings3.put(4, 4.5);
        userRatings.put(3, ratings3);

        // 创建计算器对象
        UserSimilarityCalculator calculator = new UserSimilarityCalculator(userRatings);

        // 计算用户1和用户2的相似度
        double similarity = calculator.calculateSimilarity(1, 2);
        System.out.println("用户1和用户2的相似度：" + similarity);

        // 计算用户2和用户3的相似度
        similarity = calculator.calculateSimilarity(2, 3);
        System.out.println("用户2和用户3的相似度：" + similarity);
    }
}
