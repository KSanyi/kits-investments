package hu.kits.investments.domain.asset;

import static java.util.stream.Collectors.toList;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;
import hu.kits.investments.domain.asset.Asset.AssetClass;

public class Assets {

    private final List<Asset> assetList;

    public Assets(List<Asset> assetList) {
        this.assetList = assetList;
    }

    public Stream<Asset> stream() {
        return assetList.stream();
    }

    public Asset findByTicker(String ticker) {
        return assetList.stream().filter(asset -> asset.ticker().equals(ticker)).findAny().get();
    }

    public static Assets of(Asset ... assets) {
        return new Assets(Arrays.asList(assets));
    }

    public Assets forAssetClass(AssetClass assetClass) {
        return new Assets(assetList.stream().filter(asset -> asset.assetClass() == assetClass).collect(toList()));
    }

}
