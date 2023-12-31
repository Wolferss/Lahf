package handler.onshiftaction.commons;

import org.apache.commons.lang3.StringUtils;
import org.mmocore.gameserver.configuration.config.ServerConfig;
import org.mmocore.gameserver.model.instances.MonsterInstance;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.model.instances.RaidBossInstance;
import org.mmocore.gameserver.model.reward.RewardData;
import org.mmocore.gameserver.model.reward.RewardGroup;
import org.mmocore.gameserver.model.reward.RewardList;
import org.mmocore.gameserver.model.reward.RewardType;
import org.mmocore.gameserver.network.lineage.components.HtmlMessage;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.stats.Stats;
import org.mmocore.gameserver.utils.HtmlUtils;
import org.mmocore.gameserver.utils.PlayerUtils;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public abstract class RewardListInfo
{
	private static final NumberFormat pf = NumberFormat.getPercentInstance(Locale.ENGLISH);
	private static final NumberFormat df = NumberFormat.getInstance(Locale.ENGLISH);

	static
	{
		pf.setMaximumFractionDigits(4);
		df.setMinimumFractionDigits(2);
	}

	public static void showInfo(Player player, NpcInstance npc)
	{
		final int diff = npc.calculateLevelDiffForDrop(player.isInParty() ? player.getParty().getLevel() : player.getLevel());
		double mod = npc.calcStat(Stats.REWARD_MULTIPLIER, 1., player, null);
		mod *= PlayerUtils.penaltyModifier(diff, 9);

		HtmlMessage htmlMessage = new HtmlMessage(5);
		htmlMessage.replace("%npc_name%", HtmlUtils.htmlNpcName(npc.getNpcId()));

		// TODO починить
		//@SuppressWarnings("unused")
		//boolean icons = player.getPlayerVariables().getBoolean("DroplistIcons");

		if(mod <= 0)
		{
			htmlMessage.setFile("actions/rewardlist_to_weak.htm");
			player.sendPacket(htmlMessage);
			return;
		}

		if(npc.getTemplate().getRewards().isEmpty())
		{
			htmlMessage.setFile("actions/rewardlist_empty.htm");
			player.sendPacket(htmlMessage);
			return;
		}

		htmlMessage.setFile("actions/rewardlist_info.htm");

		StringBuilder builder = new StringBuilder(100);
		for(Map.Entry<RewardType, RewardList> entry : npc.getTemplate().getRewards().entrySet())
		{
			RewardList rewardList = entry.getValue();

			switch(entry.getKey())
			{
				case RATED_GROUPED:
					ratedGroupedRewardList(builder, npc, rewardList, player, mod);
					break;
				case NOT_RATED_GROUPED:
				case EVENT:
					notRatedGroupedRewardList(builder, rewardList, mod);
					break;
				case NOT_RATED_NOT_GROUPED:
					notGroupedRewardList(builder, rewardList, 1.0, mod);
					break;
				case SWEEP:
					notGroupedRewardList(builder, rewardList, ServerConfig.RATE_DROP_SPOIL * player.getRateSpoil(), mod);
					break;
			}
		}
		if (npc instanceof MonsterInstance) {
			int champion = ((MonsterInstance) npc).getChampion();
			if (champion == 1)
				notGroupedRewardList(builder, MonsterInstance.blueChampionDrop, 1, 1);
			else if (champion == 2)
				notGroupedRewardList(builder, MonsterInstance.redChampionDrop, 1, 1);
		}
		htmlMessage.replace("%info%", builder.toString());
		player.sendPacket(htmlMessage);
	}

	public static void ratedGroupedRewardList(StringBuilder tmp, NpcInstance npc, RewardList list, Player player, double mod)
	{
		tmp.append("<table width=270 border=0>");
		tmp.append("<tr><td><table width=270 border=0><tr><td><font color=\"aaccff\">").append(list.getType()).append("</font></td></tr></table></td></tr>");
		tmp.append("<tr><td><img src=\"L2UI.SquareWhite\" width=270 height=1> </td></tr>");
		tmp.append("<tr><td><img src=\"L2UI.SquareBlank\" width=270 height=10> </td></tr>");

		for(RewardGroup g : list)
		{
			List<RewardData> items = g.getItems();
			double gchance = g.getChance();
			double gmod = mod;
			double grate;
			double gmult;

			double rateDrop = (npc instanceof RaidBossInstance ? ServerConfig.RATE_DROP_RAIDBOSS : npc.isSiegeGuard() ? ServerConfig.RATE_DROP_SIEGE_GUARD : ServerConfig.RATE_DROP_ITEMS * player.getRateItems());
			double rateAdena = ServerConfig.RATE_DROP_ADENA * player.getRateAdena();

			if(g.isAdena())
			{
				if(rateAdena == 0)
				{
					continue;
				}

				grate = rateAdena;

				if(gmod > 10)
				{
					gmod *= g.getChance() / RewardList.MAX_CHANCE;
					gchance = RewardList.MAX_CHANCE;
				}

				grate *= gmod;
			}
			else
			{
				if(rateDrop == 0)
				{
					continue;
				}

				grate = rateDrop;

				if (npc instanceof RaidBossInstance && g.isBossRatedItem())
					grate *= player.getRateItems();

				if(g.notRate())
				{
					grate = Math.min(gmod, 1.0);
				}
				else
				{
					grate *= gmod;
				}
			}

//			gmult = Math.ceil(grate); // wtf?
			gmult = grate;

			tmp.append("<tr><td><img src=\"L2UI.SquareBlank\" width=270 height=10> </td></tr>");
			tmp.append("<tr><td>");
			tmp.append("<table width=270 border=0 bgcolor=333333>");
			tmp.append("<tr><td width=170><font color=\"a2a0a2\">Group Chance: </font><font color=\"b09979\">").append(pf.format(gchance / RewardList.MAX_CHANCE)).append("</font></td>");
			tmp.append("<td width=100 align=right>");
			tmp.append("</td></tr>");
			tmp.append("</table>").append("</td></tr>");

			tmp.append("<tr><td><table>");
			for(RewardData d : items)
			{
				double imult = d.notRate() ? 1.0 : gmult;
				String icon = d.getItem().getIcon();
				if(icon == null || icon.equals(StringUtils.EMPTY))
				{
					icon = "icon.etc_question_mark_i00";
				}
				tmp.append("<tr><td width=32><img src=").append(icon).append(" width=32 height=32></td><td width=238>").append(HtmlUtils.htmlItemName(d.getItemId())).append("<br1>");
				tmp.append("<font color=\"b09979\">[").append(Math.round(d.getMinDrop() * (g.isAdena() ? gmult : d.notRate() ? 1.0 : imult))).append("..").append(Math.round(d.getMaxDrop() * imult)).append("]&nbsp;");
				tmp.append(pf.format(d.getChance() / RewardList.MAX_CHANCE)).append("</font></td></tr>");
			}
			tmp.append("</table></td></tr>");
		}

		tmp.append("</table>");
	}

	public static void ratedGroupedRewardList0(StringBuilder tmp, NpcInstance npc, RewardList list, Player player, double mod)
	{
		tmp.append("<table width=270 border=0>");
		tmp.append("<tr><td><table width=270 border=0><tr><td><font color=\"aaccff\">").append(list.getType()).append("</font></td></tr></table></td></tr>");
		tmp.append("<tr><td><img src=\"L2UI.SquareWhite\" width=270 height=1> </td></tr>");
		tmp.append("<tr><td><img src=\"L2UI.SquareBlank\" width=270 height=10> </td></tr>");

		for(RewardGroup g : list)
		{
			List<RewardData> items = g.getItems();

			tmp.append("<tr><td><img src=\"L2UI.SquareBlank\" width=270 height=10> </td></tr>");
			tmp.append("<tr><td>");
			tmp.append("<table width=270 border=0 bgcolor=333333>");
			tmp.append("<tr><td width=170><font color=\"a2a0a2\">Group Chance: </font><font color=\"b09979\">").append(pf.format(g.getChance() / RewardList.MAX_CHANCE)).append("</font></td>");
			tmp.append("<td width=100 align=right>");
			tmp.append("</td></tr>");
			tmp.append("</table>").append("</td></tr>");

			tmp.append("<tr><td><table>");
			for(RewardData d : items)
			{
				String icon = d.getItem().getIcon();
				if(icon == null || icon.equals(StringUtils.EMPTY))
				{
					icon = "icon.etc_question_mark_i00";
				}
				tmp.append("<tr><td width=32><img src=").append(icon).append(" width=32 height=32></td><td width=238>").append(HtmlUtils.htmlItemName(d.getItemId())).append("<br1>");
				tmp.append("<font color=\"b09979\">[").append(Math.round(d.getMinDrop())).append("..").append(Math.round(d.getMaxDrop())).append("]&nbsp;");
				tmp.append(pf.format(d.getChance() / RewardList.MAX_CHANCE)).append("</font></td></tr>");
			}
			tmp.append("</table></td></tr>");
		}

		tmp.append("</table>");
	}


	public static void notRatedGroupedRewardList(StringBuilder tmp, RewardList list, double mod)
	{
		tmp.append("<table width=270 border=0>");
		tmp.append("<tr><td><table width=270 border=0><tr><td><font color=\"aaccff\">").append(list.getType()).append("</font></td></tr></table></td></tr>");
		tmp.append("<tr><td><img src=\"L2UI.SquareWhite\" width=270 height=1> </td></tr>");
		tmp.append("<tr><td><img src=\"L2UI.SquareBlank\" width=270 height=10> </td></tr>");

		for(RewardGroup g : list)
		{
			List<RewardData> items = g.getItems();
			double gchance = g.getChance();

			tmp.append("<tr><td><img src=\"L2UI.SquareBlank\" width=270 height=10> </td></tr>");
			tmp.append("<tr><td>");
			tmp.append("<table width=270 border=0 bgcolor=333333>");
			tmp.append("<tr><td width=170><font color=\"a2a0a2\">Group Chance: </font><font color=\"b09979\">").append(pf.format(gchance / RewardList.MAX_CHANCE)).append("</font></td>");
			tmp.append("<td width=100 align=right>");
			tmp.append("</td></tr>");
			tmp.append("</table>").append("</td></tr>");

			tmp.append("<tr><td><table>");
			for(RewardData d : items)
			{
				String icon = d.getItem().getIcon();
				if(icon == null || icon.equals(StringUtils.EMPTY))
				{
					icon = "icon.etc_question_mark_i00";
				}
				tmp.append("<tr><td width=32><img src=").append(icon).append(" width=32 height=32></td><td width=238>").append(HtmlUtils.htmlItemName(d.getItemId())).append("<br1>");
				tmp.append("<font color=\"b09979\">[").append(Math.round(d.getMinDrop())).append("..").append(Math.round(d.getMaxDrop())).append("]&nbsp;");
				tmp.append(pf.format(d.getChance() / RewardList.MAX_CHANCE)).append("</font></td></tr>");
			}
			tmp.append("</table></td></tr>");
		}

		tmp.append("</table>");
	}

	public static void notGroupedRewardList(StringBuilder tmp, RewardList list, double rate, double mod)
	{
		tmp.append("<table width=270 border=0>");
		tmp.append("<tr><td><img src=\"L2UI.SquareBlank\" width=270 height=10> </td></tr>");
		tmp.append("<tr><td><table width=270 border=0><tr><td><font color=\"aaccff\">").append(list.getType()).append("</font></td></tr></table></td></tr>");
		tmp.append("<tr><td><img src=\"L2UI.SquareWhite\" width=270 height=1> </td></tr>");
		tmp.append("<tr><td><img src=\"L2UI.SquareBlank\" width=270 height=10> </td></tr>");

		tmp.append("<tr><td><table>");
		for(RewardGroup g : list)
		{
			List<RewardData> items = g.getItems();
			double gmod = mod;
			double grate;
			double gmult;

			if(rate == 0)
			{
				continue;
			}

			grate = rate;

			if(g.notRate())
			{
				grate = Math.min(gmod, 1.0);
			}
			else
			{
				grate *= gmod;
			}

			gmult = Math.ceil(grate);

			for(RewardData d : items)
			{
				double imult = d.notRate() ? 1.0 : gmult;
				String icon = d.getItem().getIcon();
				if(icon == null || icon.equals(StringUtils.EMPTY))
				{
					icon = "icon.etc_question_mark_i00";
				}
				tmp.append("<tr><td width=32><img src=").append(icon).append(" width=32 height=32></td><td width=238>").append(HtmlUtils.htmlItemName(d.getItemId())).append("<br1>");
				tmp.append("<font color=\"b09979\">[").append(d.getMinDrop()).append("..").append(Math.round(d.getMaxDrop() * imult)).append("]&nbsp;");
				tmp.append(pf.format(d.getChance() / RewardList.MAX_CHANCE)).append("</font></td></tr>");
			}
		}

		tmp.append("</table></td></tr>");
		tmp.append("</table>");
	}
}