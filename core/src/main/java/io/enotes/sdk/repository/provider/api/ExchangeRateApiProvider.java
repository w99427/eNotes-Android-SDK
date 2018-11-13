package io.enotes.sdk.repository.provider.api;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.support.annotation.IntDef;
import android.support.annotation.StringDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.math.BigDecimal;

import io.enotes.sdk.constant.Status;
import io.enotes.sdk.repository.api.ExchangeRateApiService;
import io.enotes.sdk.repository.api.RetrofitFactory;
import io.enotes.sdk.repository.api.entity.EntExchangeRateEntity;
import io.enotes.sdk.repository.api.entity.EntExchangeRateUSDEntity;
import io.enotes.sdk.repository.api.entity.response.exchange.CryptoCompareEntity;
import io.enotes.sdk.repository.base.Resource;

import static io.enotes.sdk.constant.ErrorCode.NET_ERROR;

public class ExchangeRateApiProvider extends BaseApiProvider {
    public static final String DIGICCY_BTC = "BTC";
    public static final String DIGICCY_ETH = "ETH";
    public static final String DIGICCY_GUSD = "GUSD";
    @Retention(RetentionPolicy.SOURCE)
    @StringDef({DIGICCY_BTC, DIGICCY_ETH, DIGICCY_GUSD})
    public @interface RateMode {
    }

    private ExchangeRateApiService exchangeRateApiService;
    private EntExchangeRateUSDEntity entExchangeRateUSDEntity;

    public ExchangeRateApiProvider() {
        exchangeRateApiService = RetrofitFactory.getExchangeRateService();
    }

    public LiveData<Resource<EntExchangeRateEntity>> getExchangeRate(@RateMode String digiccy) {
        if (digiccy.contains(DIGICCY_GUSD)) {
            return addLiveDataSourceNoENotes(getExchangeRateGUSD1st(digiccy), getExchangeRate4ur(digiccy));
        } else {
            return addLiveDataSourceNoENotes(getExchangeRate1st(digiccy), getExchangeRate2nd(digiccy), getExchangeRate3rd(digiccy), getExchangeRate4ur(digiccy));
        }
    }

    private LiveData<Resource<EntExchangeRateEntity>> getExchangeRate1st(String digiccy) {
        MediatorLiveData<Resource<EntExchangeRateEntity>> mediatorLiveData = new MediatorLiveData<>();
        mediatorLiveData.addSource(exchangeRateApiService.getExchangeRateForCoinbase(digiccy.toUpperCase()), (resource -> {
            if (resource.isSuccessful()) {
                mediatorLiveData.postValue(Resource.success(resource.body.parseToENotesEntity()));
            } else {
                mediatorLiveData.postValue(Resource.error(NET_ERROR, resource.errorMessage));
            }
        }));
        return mediatorLiveData;
    }

    private LiveData<Resource<EntExchangeRateEntity>> getExchangeRate2nd(String digiccy) {
        MediatorLiveData<Resource<EntExchangeRateEntity>> mediatorLiveData = new MediatorLiveData<>();
        String symbol = digiccy.equals(DIGICCY_BTC) ? "btc_usd" : "eth_usd";
        mediatorLiveData.addSource(exchangeRateApiService.getExchangeRateOkex(symbol), (resource -> {
            if (resource.isSuccessful()) {
                mediatorLiveData.addSource(getExchangeRateUSD(), (resource1 -> {
                    if (resource1.status == Status.SUCCESS) {
                        EntExchangeRateEntity rateEntity = new EntExchangeRateEntity();
                        rateEntity.setDigiccy(digiccy);
                        rateEntity.setExchange("okex");
                        EntExchangeRateEntity.Data exData = new EntExchangeRateEntity.Data();
                        exData.setUsd(resource.body.getFuture_index());
                        exData.setEur(new BigDecimal(resource.body.getFuture_index()).multiply(new BigDecimal(resource1.data.getEur())).toString());
                        exData.setCny(new BigDecimal(resource.body.getFuture_index()).multiply(new BigDecimal(resource1.data.getCny())).toString());
                        exData.setJpy(new BigDecimal(resource.body.getFuture_index()).multiply(new BigDecimal(resource1.data.getJpy())).toString());
                        rateEntity.setData(exData);
                        mediatorLiveData.postValue(Resource.success(rateEntity));
                    } else {
                        mediatorLiveData.postValue(Resource.error(NET_ERROR, resource.errorMessage));
                    }
                }));
            } else {
                mediatorLiveData.postValue(Resource.error(NET_ERROR, resource.errorMessage));
            }
        }));
        return mediatorLiveData;
    }

    private LiveData<Resource<EntExchangeRateEntity>> getExchangeRate3rd(String digiccy) {
        MediatorLiveData<Resource<EntExchangeRateEntity>> mediatorLiveData = new MediatorLiveData<>();
        mediatorLiveData.addSource(exchangeRateApiService.getExchangeRateForBitz(digiccy.toLowerCase()), (resource -> {
            if (resource.isSuccessful()) {
                mediatorLiveData.postValue(Resource.success(resource.body.parseToENotesEntity()));
            } else {
                mediatorLiveData.postValue(Resource.error(NET_ERROR, resource.errorMessage));
            }
        }));
        return mediatorLiveData;
    }

    private LiveData<Resource<EntExchangeRateEntity>> getExchangeRate4ur(String digiccy) {
        MediatorLiveData<Resource<EntExchangeRateEntity>> mediatorLiveData = new MediatorLiveData<>();
        mediatorLiveData.addSource(exchangeRateApiService.getExchangeRateForCryptocompare(digiccy.toUpperCase()), (resource -> {
            if (resource.isSuccessful()) {
                EntExchangeRateEntity rateEntity = new EntExchangeRateEntity();
                rateEntity.setExchange("cryptocompare");
                rateEntity.setDigiccy(digiccy.toUpperCase());
                CryptoCompareEntity entity = resource.body.get(digiccy.toUpperCase());
                EntExchangeRateEntity.Data data = new EntExchangeRateEntity.Data();
                data.setUsd(entity.getUSD());
                data.setEur(entity.getEUR());
                data.setCny(entity.getCNY());
                data.setJpy(entity.getJPY());
                rateEntity.setData(data);
                mediatorLiveData.postValue(Resource.success(rateEntity));
            } else {
                mediatorLiveData.postValue(Resource.error(NET_ERROR, resource.errorMessage));
            }
        }));
        return mediatorLiveData;
    }


    private LiveData<Resource<EntExchangeRateEntity>> getExchangeRateGUSD1st(String digiccy) {
        MediatorLiveData<Resource<EntExchangeRateEntity>> mediatorLiveData = new MediatorLiveData<>();
        mediatorLiveData.addSource(exchangeRateApiService.getExchangeRateGUSDBTCForOkex(), (resource0 -> {
            if (resource0.isSuccessful()) {
                mediatorLiveData.addSource(exchangeRateApiService.getExchangeRateOkex("btc_usd"), (resource -> {
                    if (resource.isSuccessful()) {
                        mediatorLiveData.addSource(getExchangeRateUSD(), (resource1 -> {
                            if (resource1.status == Status.SUCCESS) {
                                EntExchangeRateEntity rateEntity = new EntExchangeRateEntity();
                                rateEntity.setDigiccy(digiccy);
                                rateEntity.setExchange("okex");
                                EntExchangeRateEntity.Data exData = new EntExchangeRateEntity.Data();
                                String gusd2usd = new BigDecimal(resource0.body.getLast()).multiply(new BigDecimal(resource.body.getFuture_index())).toString();
                                exData.setUsd(gusd2usd);
                                exData.setEur(new BigDecimal(gusd2usd).multiply(new BigDecimal(resource1.data.getEur())).toString());
                                exData.setCny(new BigDecimal(gusd2usd).multiply(new BigDecimal(resource1.data.getCny())).toString());
                                exData.setJpy(new BigDecimal(gusd2usd).multiply(new BigDecimal(resource1.data.getJpy())).toString());
                                rateEntity.setData(exData);
                                mediatorLiveData.postValue(Resource.success(rateEntity));
                            } else {
                                mediatorLiveData.postValue(Resource.error(NET_ERROR, resource.errorMessage));
                            }
                        }));
                    } else {
                        mediatorLiveData.postValue(Resource.error(NET_ERROR, resource.errorMessage));
                    }
                }));

            } else {
                mediatorLiveData.postValue(Resource.error(NET_ERROR, resource0.errorMessage));
            }
        }));
        return mediatorLiveData;
    }


    private LiveData<Resource<EntExchangeRateUSDEntity>> getExchangeRateUSD() {
        return addLiveDataSourceNoENotes(getExchangeRateUSD1st(), getExchangeRateUSD2nd());
    }

    private LiveData<Resource<EntExchangeRateUSDEntity>> getExchangeRateUSD1st() {
        MediatorLiveData<Resource<EntExchangeRateUSDEntity>> mediatorLiveData = new MediatorLiveData<>();
        if (entExchangeRateUSDEntity != null) {
            mediatorLiveData.postValue(Resource.success(entExchangeRateUSDEntity));
        }
        mediatorLiveData.addSource(exchangeRateApiService.getExchangeRateUSDForCryptocompare(), resource -> {
            if (resource.isSuccessful() && resource.body != null) {
                CryptoCompareEntity entity = resource.body.get("USD");
                EntExchangeRateUSDEntity rateUSDEntity = new EntExchangeRateUSDEntity();
                rateUSDEntity.setEur(entity.getEUR());
                rateUSDEntity.setCny(entity.getCNY());
                rateUSDEntity.setJpy(entity.getJPY());
                entExchangeRateUSDEntity = rateUSDEntity;
                mediatorLiveData.postValue(Resource.success(entExchangeRateUSDEntity));
            } else {
                mediatorLiveData.postValue(Resource.error(NET_ERROR, resource.errorMessage));
            }
        });
        return mediatorLiveData;
    }

    private LiveData<Resource<EntExchangeRateUSDEntity>> getExchangeRateUSD2nd() {
        MediatorLiveData<Resource<EntExchangeRateUSDEntity>> mediatorLiveData = new MediatorLiveData<>();
        if (entExchangeRateUSDEntity != null) {
            mediatorLiveData.postValue(Resource.success(entExchangeRateUSDEntity));
        }
        mediatorLiveData.addSource(exchangeRateApiService.getExchangeRateUSDForBitz(), (resource -> {
            if (resource.isSuccessful() && resource.body != null && resource.body.getStatus() == 200) {
                EntExchangeRateUSDEntity rateUSDEntity = new EntExchangeRateUSDEntity();
                rateUSDEntity.setCny(resource.body.getData().getUsd_cny().getRate());
                rateUSDEntity.setEur(resource.body.getData().getUsd_eur().getRate());
                rateUSDEntity.setJpy(resource.body.getData().getUsd_jpy().getRate());
                entExchangeRateUSDEntity = rateUSDEntity;
                mediatorLiveData.postValue(Resource.success(entExchangeRateUSDEntity));
            } else if (resource.body != null && resource.body.getStatus() != 200) {
                mediatorLiveData.postValue(Resource.error(NET_ERROR, resource.body.getMsg()));
            } else {
                mediatorLiveData.postValue(Resource.error(NET_ERROR, resource.errorMessage));
            }
        }));
        return mediatorLiveData;
    }
}
