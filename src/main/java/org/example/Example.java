package org.example;

import com.azure.ai.textanalytics.TextAnalyticsClient;
import com.azure.ai.textanalytics.TextAnalyticsClientBuilder;
import com.azure.ai.textanalytics.models.*;
import com.azure.core.credential.AzureKeyCredential;

public class Example {
    private static String LANGUAGE_KEY = System.getenv("LANGUAGE_KEY");
    private static String LANGUAGE_END_POINT = System.getenv("LANGUAGE_ENDPOINT");

    public static void main(String[] args) {

        System.out.println(LANGUAGE_KEY);
        System.out.println(LANGUAGE_END_POINT);
        TextAnalyticsClient client = authenticateClient(LANGUAGE_KEY, LANGUAGE_END_POINT);
        sentimentAnalysisWithOpinionMiningExample(client);
    }

    static TextAnalyticsClient authenticateClient(String key, String endpoint) {
        return new TextAnalyticsClientBuilder()
                .credential(new AzureKeyCredential(key))
                .endpoint(endpoint)
                .buildClient();
    }

    static void sentimentAnalysisWithOpinionMiningExample(TextAnalyticsClient client) {
        String document = "My name is santanu barua. The food and service were unacceptable. The concierge was nice, however.";
        System.out.printf("Document = %s%n", document);

        AnalyzeSentimentOptions options = new AnalyzeSentimentOptions().setIncludeOpinionMining(true);
        final DocumentSentiment documentSentiment = client.analyzeSentiment(document, "en", options);
        SentimentConfidenceScores scores = documentSentiment.getConfidenceScores();

        System.out.printf(
                "Recognized document sentiment: %s, positive score: %f, neural score: %f, negative score: %f.%n",
                documentSentiment.getSentiment(), scores.getPositive(), scores.getNeutral(), scores.getNegative()
        );

        documentSentiment.getSentences().forEach(sentenceSentiment -> {
            SentimentConfidenceScores sentenceScores = sentenceSentiment.getConfidenceScores();
            System.out.printf("\tSentence sentiment: %s, positive score: %f, neutral score: %f, negative score: %f.%n",
                    sentenceSentiment.getSentiment(), sentenceScores.getPositive(), sentenceScores.getNeutral(), sentenceScores.getNegative());


            sentenceSentiment.getOpinions().forEach(option -> {
                TargetSentiment targetSentiment = option.getTarget();
                System.out.printf("\t\t Target sentiment: %s, target text: %s%n", targetSentiment.getSentiment(), targetSentiment.getText());

                for (AssessmentSentiment assessmentSentiment : option.getAssessments()) {
                    System.out.printf("\t\t\t'%s' assessment sentiment because of \"%s\". Is the assessment negated: %s.%n",
                            assessmentSentiment.getSentiment(), assessmentSentiment.getText(), assessmentSentiment.isNegated());
                }
            });
        });
    }
}
