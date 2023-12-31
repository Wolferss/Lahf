package org.jts.dataparser.data.holder.auctiondata;

import org.jts.dataparser.data.annotations.Element;
import org.jts.dataparser.data.annotations.array.IntArray;
import org.jts.dataparser.data.annotations.value.IntValue;

import java.util.List;

/**
 * @author : Camelion
 * @date : 25.08.12 18:51
 */
public class Auction {
    @IntValue
    public int npc_id; // NPC ID
    @IntValue
    public int auction_doing; // Неизвестно, принимает значения 0 или 1
    @IntArray
    public int[] auction_week_day; // Дни, (день?) недели аукциона ( 1 =
    // понедельник, 2 = вторник, итд.)
    @IntValue
    public int auction_start_time; // Время начала аукциона. Неизвестный формат
    @IntValue
    public int auction_duration; // Продолжительность аукциона ( в минутах )
    @IntValue
    public int item_count; // Кол-во предметов?
    @Element(start = "item_begin", end = "item_end")
    public List<AuctionItem> auctionItems;
}
