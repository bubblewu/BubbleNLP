package com.bubble.bnlp.classify.perceptron.seg;

import java.util.Objects;

/**
 * @author wugang
 * date: 2019-01-07 16:49
 **/
public class Feature {

    enum FeatureType {
        /*
        My features:
            U: -2 -1 0 1 2: Ci-2 Ci-1 Ci Ci+1 Ci+2
            B: -2 -1 0 1 2: Ci-2i-1 Ci-1i Ci-1i+1 Cii+1 Ci+1i+2
            Tag: -2 -1: Tagi-2 Tagi-1
        */
        U_2, U_1, U0, U1, U2,
        B_2, B_1, B0, B1, B2,
        Tag_2, Tag_1
    }

    private FeatureType featureType;
    private String featureWord;

    Feature(FeatureType featureType, char... featureWord) {
        this.featureType = featureType;
        this.featureWord = String.valueOf(featureWord);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Feature feature = (Feature) o;
        return featureType == feature.featureType &&
                Objects.equals(featureWord, feature.featureWord);
    }

    @Override
    public int hashCode() {
        return Objects.hash(featureType, featureWord);
    }

}
