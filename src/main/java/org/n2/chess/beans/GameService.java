/*******************************************************************************
 * Copyright (c) 2011 Daniel Murygin.
 *
 * This program is free software: you can redistribute it and/or 
 * modify it under the terms of the GNU Lesser General Public License 
 * as published by the Free Software Foundation, either version 3 
 * of the License, or (at your option) any later version.
 * This program is distributed in the hope that it will be useful,    
 * but WITHOUT ANY WARRANTY; without even the implied warranty 
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program. 
 * If not, see <http://www.gnu.org/licenses/>.
 * 
 * Contributors:
 *     Daniel Murygin <dm[at]sernet[dot]de> - initial API and implementation
 ******************************************************************************/
package org.n2.chess.beans;

import java.io.Serializable;
import java.util.Calendar;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.n2.chess.beans.hibernate.Game;
import org.n2.chess.beans.hibernate.IGameDao;
import org.n2.chess.beans.hibernate.User;
import org.n2.chess.model.Square;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author Daniel Murygin <dm[at]sernet[dot]de>
 *
 */
@Service("gameService")
public class GameService implements IGameService, Serializable {

    public static final String FEN_START = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";
    
    @Autowired
    IGameDao gameDao;
    
    @Autowired
    IUserService userService;
    
    /* (non-Javadoc)
     * @see org.n2.chess.beans.IGameService#loadGames()
     */
    @Override
    public List<Game> loadGames(User user) {
        
        DetachedCriteria crit = DetachedCriteria.forClass(Game.class);
        crit.add(Restrictions.or(
                Restrictions.eq("playerWhite.id", user.getId()), 
                Restrictions.eq("playerBlack.id", user.getId())));
        crit.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
        return getGameDao().find(crit);
    }
    
    /* (non-Javadoc)
     * @see org.n2.chess.beans.IGameService#create(org.n2.chess.beans.hibernate.User, java.lang.String)
     */
    @Override
    public Game create(User userWhite, String emailBlack) {
        User userBlack = getUserService().findUserByEmail(emailBlack);
        Game game = null;
        if(userBlack!=null && userWhite!=null) {
            game = create(userWhite, userBlack);
        }
        return game;
    }
    
    /* (non-Javadoc)
     * @see org.n2.chess.beans.IGameService#create(org.n2.chess.beans.hibernate.User, java.lang.String)
     */
    @Override
    public Game create(String emailWhite, User userBlack) {
        User userWhite = getUserService().findUserByEmail(emailWhite);
        Game game = null;
        if(userBlack!=null && userWhite!=null) {
            game = create(userWhite, userBlack);
        }
        return game;
    }

    /**
     * @param playerWhite
     * @param playerBlack
     * @return
     */
    public Game create(User playerWhite, User playerBlack) {
        Game game = new Game();
        game.setPlayerBlack(playerBlack);
        game.setPlayerWhite(playerWhite);
        game.setFen(FEN_START);
        game.setStartDate(Calendar.getInstance().getTime());
        game.setStatus(Game.WHITE);
        getGameDao().save(game);
        return game;
    }
    
    /* (non-Javadoc)
     * @see org.n2.chess.beans.IGameService#saveGame(org.n2.chess.beans.hibernate.Game)
     */
    @Override
    public void updateGame(Game game) {
        getGameDao().update(game); 
    }

    /**
     * @return the gameDao
     */
    public IGameDao getGameDao() {
        return gameDao;
    }

    /**
     * @param gameDao the gameDao to set
     */
    public void setGameDao(IGameDao gameDao) {
        this.gameDao = gameDao;
    }

    /**
     * @return the userService
     */
    public IUserService getUserService() {
        return userService;
    }

    /**
     * @param userService the userService to set
     */
    public void setUserService(IUserService userService) {
        this.userService = userService;
    }

}
