import React from 'react';
import { 
  Grid, 
  Paper, 
  Typography, 
  Box,
  Card,
  CardContent,
  List,
  ListItem,
  ListItemText,
  Divider
} from '@mui/material';
import { Link } from 'react-router-dom';

const DashboardPage = () => {
  // 임시 데이터 (나중에 API로 대체)
  const stats = {
    totalReviews: 150,
    recentReviews: [
      { id: 1, hospitalName: '행복동물병원', rating: 5, date: '2024-04-29' },
      { id: 2, hospitalName: '사랑동물병원', rating: 4, date: '2024-04-28' },
      { id: 3, hospitalName: '우리동물병원', rating: 3, date: '2024-04-27' },
    ]
  };

  return (
    <Grid container spacing={3}>
      {/* 통계 카드 */}
      <Grid item xs={12} md={6} lg={3}>
        <Card>
          <CardContent>
            <Typography color="textSecondary" gutterBottom>
              전체 리뷰 수
            </Typography>
            <Typography variant="h4">
              {stats.totalReviews}
            </Typography>
          </CardContent>
        </Card>
      </Grid>

      {/* 최근 리뷰 목록 */}
      <Grid item xs={12}>
        <Paper sx={{ p: 2 }}>
          <Typography variant="h6" gutterBottom>
            최근 리뷰
          </Typography>
          <List>
            {stats.recentReviews.map((review, index) => (
              <React.Fragment key={review.id}>
                <ListItem 
                  component={Link} 
                  to={`/reviews/${review.id}`}
                  sx={{ 
                    textDecoration: 'none',
                    color: 'inherit',
                    '&:hover': {
                      backgroundColor: 'rgba(0, 0, 0, 0.04)'
                    }
                  }}
                >
                  <ListItemText
                      primary={review.hospitalName}
                      secondary={
                        <Box sx={{ display: 'flex', justifyContent: 'space-between' }}>
                          <Typography variant="body2" component="span">
                            평점: {'★'.repeat(review.rating)}
                          </Typography>
                          <Typography variant="body2" color="text.secondary" component="span">
                            {review.date}
                          </Typography>
                        </Box>
                    }
                  />
                </ListItem>
                {index < stats.recentReviews.length - 1 && <Divider />}
              </React.Fragment>
            ))}
          </List>
        </Paper>
      </Grid>
    </Grid>
  );
};

export default DashboardPage; 