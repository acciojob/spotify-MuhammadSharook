package com.driver;

import java.util.*;

import org.springframework.stereotype.Repository;

@Repository
public class SpotifyRepository {
    public HashMap<Artist, List<Album>> artistAlbumMap;
    public HashMap<Album, List<Song>> albumSongMap;
    public HashMap<Playlist, List<Song>> playlistSongMap;
    public HashMap<Playlist, List<User>> playlistListenerMap;
    public HashMap<User, Playlist> creatorPlaylistMap;
    public HashMap<User, List<Playlist>> userPlaylistMap;
    public HashMap<Song, List<User>> songLikeMap;

    public List<User> users;
    public List<Song> songs;
    public List<Playlist> playlists;
    public List<Album> albums;
    public List<Artist> artists;

    public SpotifyRepository() {
        //To avoid hitting apis multiple times, initialize all the hashmaps here with some dummy data
        artistAlbumMap = new HashMap<>();
        albumSongMap = new HashMap<>();
        playlistSongMap = new HashMap<>();
        playlistListenerMap = new HashMap<>();
        creatorPlaylistMap = new HashMap<>();
        userPlaylistMap = new HashMap<>();
        songLikeMap = new HashMap<>();

        users = new ArrayList<>();
        songs = new ArrayList<>();
        playlists = new ArrayList<>();
        albums = new ArrayList<>();
        artists = new ArrayList<>();
    }

    public User createUser(String name, String mobile) {
        User user = new User(name, mobile);
        users.add(user);
        return user;
    }

    public Artist createArtist(String name) {
        Artist artist = new Artist(name);
        artists.add(artist);
        return artist;
    }

    public Album createAlbum(String title, String artistName) {
        Artist a = null;
        for (Artist artist : artists) {
            if (artist.getName().equals(artistName)) {
                a = artist;
                break;
            }
        }

        if (a == null) {
            a = createArtist(artistName);
            Album album = new Album(title);
            albums.add(album);
            List<Album> l = new ArrayList<>();
            l.add(album);
            artistAlbumMap.put(a, l);
            return album;
        } else {
            Album album = new Album(title);
            albums.add(album);
            List<Album> l = new ArrayList<>();
            l.add(album);
            artistAlbumMap.put(a, l);
            return album;
        }
    }

    public Song createSong(String title, String albumName, int length) throws Exception {
        Album a = null;
        for (Album album : albums) {
            if (album.getTitle().equals(albumName)) {
                a = album;
                break;
            }
        }
        if (a == null) {
            throw new Exception("Album does not exist");
        } else {
            Song song = new Song(title, length);
            song.setLikes(0);
            songs.add(song);
            List<Song> l;
            if (albumSongMap.containsKey(a)) {
                l = albumSongMap.get(a);
            } else {
                l = new ArrayList<>();
            }
            l.add(song);
            albumSongMap.put(a, l);
            return song;
        }
    }

    public Playlist createPlaylistOnLength(String mobile, String title, int length) throws Exception {
        User u = null;
        for (User user : users) {
            if (user.getMobile().equals(mobile)) {
                u = user;
                break;
            }
        }
        if (u == null) {
            throw new Exception("User does not exist");
        } else {
            Playlist playlist = new Playlist(title);
            playlists.add(playlist);
            List<Song> l = new ArrayList<>();
            for (Song song : songs) {
                if (song.getLength() == length) {
                    l.add(song);
                }
            }
            playlistSongMap.put(playlist, l);
            List<User> l1 = new ArrayList<>();
            l1.add(u);
            playlistListenerMap.put(playlist, l1);
            creatorPlaylistMap.put(u, playlist);

            List<Playlist> l2;
            if (userPlaylistMap.containsKey(u)) {
                l2 = userPlaylistMap.get(u);
            } else {
                l2 = new ArrayList<>();
            }
            l2.add(playlist);
            userPlaylistMap.put(u, l2);
            return playlist;
        }
    }

    public Playlist createPlaylistOnName(String mobile, String title, List<String> songTitles) throws Exception {
        for(Playlist playlist : playlists){
            if(playlist.getTitle().equals(title))
                return  playlist;
        }
        Playlist playlist = new Playlist(title);
        // adding playlist to playlists list
        playlists.add(playlist);

        List<Song> l = new ArrayList<>();
        for(Song song : songs){
            if(songTitles.contains(song.getTitle())){
                l.add(song);
            }
        }
        playlistSongMap.put(playlist,l);

        User curUser = new User();
        boolean flag = false;
        for(User user : users){
            if(user.getMobile().equals(mobile)){
                curUser = user;
                flag = true;
                break;
            }
        }
        if (!flag){
            throw new Exception("User does not exist");
        }

        List<User> userslist = new ArrayList<>();
        if(playlistListenerMap.containsKey(playlist)){
            userslist = playlistListenerMap.get(playlist);
        }
        userslist.add(curUser);
        playlistListenerMap.put(playlist,userslist);

        creatorPlaylistMap.put(curUser,playlist);

        List<Playlist>userplaylists = new ArrayList<>();
        if(userPlaylistMap.containsKey(curUser)){
            userplaylists = userPlaylistMap.get(curUser);
        }
        userplaylists.add(playlist);
        userPlaylistMap.put(curUser,userplaylists);

        return playlist;
    }

    public Playlist findPlaylist(String mobile, String playlistTitle) throws Exception {
        User u = null;
        for (User user : users) {
            if (user.getMobile().equals(mobile)) {
                u = user;
                break;
            }
        }
        if (u == null) {
            throw new Exception("User does not exist");
        }
        Playlist p = null;
        for (Playlist playlist : playlists) {
            if (playlist.getTitle().equals(playlistTitle)) {
                p = playlist;
                break;
            }
        }
        if (p == null) {
            throw new Exception("Playlist does not exist");
        }
        if (creatorPlaylistMap.containsKey(u)) {
            return p;
        }
        List<User> l = playlistListenerMap.get(p);
        for (User user : l) {
            if (user == u) {
                return p;
            }
        }
        l.add(u);
        playlistListenerMap.put(p, l);
        List<Playlist> l2 = userPlaylistMap.get(u);
        if (l2 == null) {
            l2 = new ArrayList<>();
        }
        l2.add(p);
        userPlaylistMap.put(u, l2);
        return p;
    }

    public Song likeSong(String mobile, String songTitle) throws Exception {
        User curUser= new User();
        boolean f1 = false;
        for(User user: users){
            if(user.getMobile().equals(mobile)){
                curUser=user;
                f1 = true;
                break;
            }
        }
        if (!f1){
            throw new Exception("User does not exist");
        }

        Song song = new Song();
        boolean f2 = false;
        for(Song cursong : songs){
            if(cursong.getTitle().equals(songTitle)){
                song=cursong;
                f2 = true;
                break;
            }
        }
        if (!f2){
            throw new Exception("Song does not exist");
        }

        List<User> users = new ArrayList<>();
        if(songLikeMap.containsKey(song)){
            users=songLikeMap.get(song);
        }
        if (!users.contains(curUser)){
            users.add(curUser);
            songLikeMap.put(song,users);
            song.setLikes(song.getLikes()+1);


            Album album = new Album();
            for(Album curAlbum : albumSongMap.keySet()){
                List<Song> temp = albumSongMap.get(curAlbum);
                if(temp.contains(song)){
                    album=curAlbum;
                    break;
                }
            }



            Artist artist = new Artist();
            for(Artist curArtist : artistAlbumMap.keySet()){
                List<Album> temp = artistAlbumMap.get(curArtist);
                if(temp.contains(album)){
                    artist=curArtist;
                    break;
                }
            }

            artist.setLikes(artist.getLikes()+1);
        }
        return song;
    }

    public String mostPopularArtist() {
        String name = "";
        int max_likes = Integer.MIN_VALUE;
        for(Artist artist : artists){
            max_likes = Math.max(max_likes,artist.getLikes());
        }
        for(Artist artist : artists){
            if(max_likes == artist.getLikes()){
                name = artist.getName();
            }
        }
        return name;
    }

    public String mostPopularSong() {
        String name = "";
        int max_likes = Integer.MIN_VALUE;
        for(Song song : songs){
            max_likes = Math.max(max_likes,song.getLikes());
        }
        for(Song song : songs){
            if(max_likes == song.getLikes())
                name = song.getTitle();
        }
        return name;
    }
}

