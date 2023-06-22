import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class CollaborativeFiltering {

    private static final int TOP_N = 5; // 推荐的电影数量
    private static final int SIMILAR_USERS = 5; // 相似用户的数量

    private Map<Integer, Map<Integer, Double>> userRatings; // 用户评分数据
    private Map<Integer, Map<Integer, Double>> userSimilarities; // 用户相似度数据

    public CollaborativeFiltering() {
        userRatings = new HashMap<>();
        userSimilarities = new HashMap<>();
    }

    public void loadData(String ratingsFile, String similaritiesFile) throws IOException {
        loadRatingsData(ratingsFile);
        loadSimilaritiesData(similaritiesFile);
    }

    private void loadRatingsData(String ratingsFile) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(ratingsFile));
        String line;
        while ((line = reader.readLine()) != null) {
            String[] data = line.split("::");
            int userId = Integer.parseInt(data[0]);
            int movieId = Integer.parseInt(data[1]);
            double rating = Double.parseDouble(data[2]);

            userRatings.putIfAbsent(userId, new HashMap<>());
            userRatings.get(userId).put(movieId, rating);
        }
        reader.close();
    }

    private void loadSimilaritiesData(String similaritiesFile) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(similaritiesFile));
        String line;
        while ((line = reader.readLine()) != null) {
            String[] data = line.split("::");
            int userId1 = Integer.parseInt(data[0]);
            int userId2 = Integer.parseInt(data[1]);
            double similarity = Double.parseDouble(data[2]);

            userSimilarities.putIfAbsent(userId1, new HashMap<>());
            userSimilarities.get(userId1).put(userId2, similarity);
        }
        reader.close();
    }

    public List<Integer> recommendMovies(int userId) {
        Map<Integer, Double> recommendations = new HashMap<>();

        // 获取相似度最高的用户
        List<Integer> similarUsers = findSimilarUsers(userId, SIMILAR_USERS);

        // 遍历相似用户的评分数据
        for (int similarUser : similarUsers) {
            Map<Integer, Double> ratings = userRatings.get(similarUser);

            // 遍历相似用户评分的电影
            for (int movieId : ratings.keySet()) {
                // 如果当前用户未评价过该电影，则计算推荐度
                if (!userRatings.get(userId).containsKey(movieId)) {
                    double similarity = userSimilarities.get(userId).get(similarUser);
                    double rating = ratings.get(movieId);
                    double recommendationScore = similarity * rating;

                    recommendations.put(movieId, recommendations.getOrDefault(movieId, 0.0) + recommendationScore);
                }
            }
        }

        // 根据推荐度排序电影
        List<Map.Entry<Integer, Double>> sortedRecommendations = new ArrayList<>(recommendations.entrySet());
        sortedRecommendations.sort(Map.Entry.comparingByValue(Comparator.reverseOrder()));

        // 返回前TOP_N个推荐电影
        List<Integer> topMovies = new ArrayList<>();
        for (int i = 0; i < TOP_N && i < sortedRecommendations.size(); i++) {
            topMovies.add(sortedRecommendations.get(i).getKey());
        }

        return topMovies;
    }

    private List<Integer> findSimilarUsers(int userId, int topN) {
        Map<Integer, Double> similarities = userSimilarities.get(userId);
        List<Map.Entry<Integer, Double>> sortedSimilarities = new ArrayList<>(similarities.entrySet());
        sortedSimilarities.sort(Map.Entry.comparingByValue(Comparator.reverseOrder()));

        List<Integer> similarUsers = new ArrayList<>();
        for (int i = 0; i < topN && i < sortedSimilarities.size(); i++) {
            similarUsers.add(sortedSimilarities.get(i).getKey());
        }

        return similarUsers;
    }

    public static void main(String[] args) {
        CollaborativeFiltering cf = new CollaborativeFiltering();
        try {
            cf.loadData("ratings.dat", "similarities.dat");

            int userId = 1;
            List<Integer> recommendedMovies = cf.recommendMovies(userId);

            System.out.println("推荐给用户 " + userId + " 的电影：");
            for (int movieId : recommendedMovies) {
                System.out.println("电影ID: " + movieId);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
