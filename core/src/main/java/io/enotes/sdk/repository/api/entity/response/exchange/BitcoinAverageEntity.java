package io.enotes.sdk.repository.api.entity.response.exchange;

import io.enotes.sdk.repository.api.entity.BaseENotesEntity;
import io.enotes.sdk.repository.api.entity.BaseThirdEntity;

public class BitcoinAverageEntity implements BaseThirdEntity{
    private String last;
    private Average averages;

    public String getLast() {
        return last;
    }

    public void setLast(String last) {
        this.last = last;
    }

    public Average getAverages() {
        return averages;
    }

    public void setAverages(Average averages) {
        this.averages = averages;
    }

    @Override
    public BaseENotesEntity parseToENotesEntity() {
        return null;
    }

    public class Average {
        private String day;

        public String getDay() {
            return day;
        }

        public void setDay(String day) {
            this.day = day;
        }
    }
}
