package dev.wolveringer.gilde;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import dev.wolveringer.client.Callback;
import dev.wolveringer.client.ProgressFuture;
import dev.wolveringer.dataserver.protocoll.packets.PacketGildMoneyHistoryAction.Action;
import dev.wolveringer.dataserver.protocoll.packets.PacketGildMoneyAction;
import dev.wolveringer.dataserver.protocoll.packets.PacketGildMoneyHistoryAction;
import dev.wolveringer.dataserver.protocoll.packets.PacketOutPacketStatus.Error;
import dev.wolveringer.sync.WaitForObject;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class GildSectionMoney {
	@Getter
	private final GildSection handle;
	private WaitForObject initObject = new WaitForObject(2);
	@Getter
	private int currentMoney = -1;
	@Getter
	private List<MoneyLogRecord> history;
	
	public void init(){
		initMoney();
		initMoneyHistory();
	}
	
	private void initMoney(){
		handle.getHandle().getConnection().getGildenMoney(handle.getHandle().getUuid(), handle.getType()).getAsync(new Callback<Integer>() {
			@Override
			public void call(Integer obj, Throwable exception) {
				initObject.done();
				if(exception != null){
					exception.printStackTrace();
					return;
				}
				currentMoney = obj;
			}
		});
	}
	private void initMoneyHistory(){
		handle.getHandle().getConnection().getGildenMoneyHistory(handle.getHandle().getUuid(), handle.getType()).getAsync(new Callback<MoneyLogRecord[]>() {
			@Override
			public void call(MoneyLogRecord[] obj, Throwable exception) {
				initObject.done();
				if(exception != null){
					exception.printStackTrace();
					return;
				}
				history = Arrays.asList(obj);
			}
		});
	}
	
	public List<MoneyLogRecord> getRecordsSorted(Comparator<MoneyLogRecord> comp){
		List<MoneyLogRecord> copiedHistory = new ArrayList<>(history);
		if(comp != null)
			Collections.sort(copiedHistory,comp);
		return copiedHistory;
	}
	
	public ProgressFuture<Error[]> log(int playerId,int amount,String message){
		return handle.getHandle().getConnection().writePacket(new PacketGildMoneyHistoryAction(handle.getHandle().getUuid(), handle.getType(), Action.ADD, playerId, amount, message));
	}
	
	public ProgressFuture<Error[]> addMoney(int money){
		return changeMoney(Math.abs(money));
	}
	public ProgressFuture<Error[]> removeMoney(int money){
		return changeMoney(-Math.abs(money));
	}
	
	@Deprecated
	public ProgressFuture<Error[]> changeMoney(int money){
		return handle.getHandle().getConnection().writePacket(new PacketGildMoneyAction(handle.getHandle().getUuid(), handle.getType(), money > 0 ? PacketGildMoneyAction.Action.ADD : PacketGildMoneyAction.Action.REMOVE, -1 /*UNUSED!*/, Math.abs(money), null /*UNUSED TOO! LOL*/));
	}
}
