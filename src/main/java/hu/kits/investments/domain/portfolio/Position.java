package hu.kits.investments.domain.portfolio;

import java.util.List;

import hu.kits.investments.domain.Asset;

public record Position(Asset asset, int quantity) {

    public static Position aggregate(Asset asset, List<TradeOrder> tradeOrders) {
        
        return tradeOrders.stream().reduce(Position.createEmpty(asset), Position::update, Position::combine);
    }
    
    public Position update(TradeOrder tradeOrder) {
        if(!tradeOrder.asset().equals(asset)) {
            throw new IllegalArgumentException("Can not update with a trade order with different asset");
        }
        
        return new Position(asset, quantity + tradeOrder.quantity());
    }
    
    Position combine(Position position) {
        if(!position.asset().equals(asset)) {
            throw new IllegalArgumentException("Can not combine with a position with different asset");
        }
        
        return new Position(asset, quantity + position.quantity());
    }
    
    public static Position createEmpty(Asset asset) {
        return new Position(asset, 0);
    }
    
}
